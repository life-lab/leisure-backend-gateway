package com.github.hicolors.leisure.backend.gateway.application.filter.pre;

import com.github.hicolors.leisure.backend.gateway.application.consts.AuthenticationConsts;
import com.github.hicolors.leisure.backend.gateway.application.exception.BackendGatewayServerException;
import com.github.hicolors.leisure.backend.gateway.application.exception.EnumCodeMessage;
import com.github.hicolors.leisure.backend.gateway.application.filter.FilterOrder;
import com.github.hicolors.leisure.member.authorization.token.TokenStore;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * AuthenticationFilter
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
@Component
@Slf4j
public class AuthenticationFilter extends ZuulFilter {

    @Value("#{'${authentication.non-require.uris:/**/**swagger**/**,/**/v2/api-docs}'.split(',')}")
    private List<String> nonRequireAuthentication;

    private PathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private TokenStore redisTokenStore;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.AUTHENTICATION_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        String uri = RequestContext.getCurrentContext().getRequest().getRequestURI();
        boolean shouldFlag = true;
        for (String excludePattern : nonRequireAuthentication) {
            if (pathMatcher.match(excludePattern, uri)) {
                shouldFlag = false;
                break;
            }
        }
        return shouldFlag;
    }

    @Override
    public Object run() {
        RequestContext reqCtx = RequestContext.getCurrentContext();
        HttpServletRequest request = reqCtx.getRequest();
        if (RequestMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        String accessToken = request.getHeader(AuthenticationConsts.HEADER_AUTHENTICATION);
        if (Objects.isNull(accessToken)) {
            throw new BackendGatewayServerException(EnumCodeMessage.ACCESS_TOKEN_IS_NULL);
        }

        Long userId = redisTokenStore.findUserIdByAccessToken(accessToken);
        if (userId == 0L) {
            throw new BackendGatewayServerException(EnumCodeMessage.ACCESS_TOKEN_IS_INVALID);
        }
        String userInfo = redisTokenStore.findUserInfoByUserId(userId);
        if (StringUtils.isBlank(userInfo)) {
            throw new BackendGatewayServerException(EnumCodeMessage.USER_INFO_IS_BLANK);
        }
        try {
            //URLEncoder防止中文乱码
            reqCtx.addZuulRequestHeader(AuthenticationConsts.HEADER_USER_ID, String.valueOf(userId));
            reqCtx.addZuulRequestHeader(AuthenticationConsts.HEADER_USER_INFO, URLEncoder.encode(userInfo, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("user info encode utf-8 error,user info : [{}]", userInfo);
        }
        log.info("access-token: [{}] , user-id: [{}], uri: [{}]", accessToken, userId, request.getRequestURI());
        return null;

    }
}