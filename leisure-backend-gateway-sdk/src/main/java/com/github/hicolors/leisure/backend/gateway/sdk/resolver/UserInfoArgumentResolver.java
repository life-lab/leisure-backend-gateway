package com.github.hicolors.leisure.backend.gateway.sdk.resolver;

import com.github.hicolors.leisure.backend.gateway.sdk.consts.AuthenticationConsts;
import com.github.hicolors.leisure.backend.gateway.sdk.exception.AuthorizationException;
import com.github.hicolors.leisure.backend.gateway.sdk.exception.EnumAuthorizationExceptionCodeMessage;
import com.github.hicolors.leisure.common.utils.JsonUtils;
import com.github.hicolors.leisure.member.authorization.token.UserInfo;
import com.github.hicolors.leisure.member.authorization.token.impl.MemberAuthorization;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.net.URLDecoder;

/**
 * AuthTokenArgumentResolver
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
@Slf4j
public class UserInfoArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(UserInfo.class);
    }

    @Override
    public UserInfo resolveArgument(MethodParameter parameter,
                                    ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest,
                                    WebDataBinderFactory binderFactory) throws Exception {
        String userInfoJson = webRequest.getHeader(AuthenticationConsts.HEADER_USER_INFO);
        MemberAuthorization userInfo;
        if (StringUtils.isBlank(userInfoJson)) {
            throw new AuthorizationException(EnumAuthorizationExceptionCodeMessage.USER_INFO_NOT_FOUND);
        }
        String userInfoJsonUTF8 = URLDecoder.decode(userInfoJson, "UTF-8");
        userInfo = JsonUtils.deserialize(userInfoJsonUTF8, MemberAuthorization.class);
        log.info("completely resolve argument {}", userInfo);
        return userInfo;
    }
}