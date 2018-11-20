package com.github.hicolors.leisure.backend.gateway.application.filter.pre;

import com.github.hicolors.leisure.backend.gateway.application.exception.BackendGatewayServerException;
import com.github.hicolors.leisure.backend.gateway.application.exception.EnumBackendGatewayCodeMessage;
import com.github.hicolors.leisure.backend.gateway.application.filter.FilterOrder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * AuthenticationFilter
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
@Component
@Slf4j
public class DenyFilter extends ZuulFilter {

    @Value("#{'${uris.deny:}'.split(',')}")
    private List<String> denyUrls;

    private PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.DENY_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        String uri = RequestContext.getCurrentContext().getRequest().getRequestURI();
        boolean shouldFlag = false;
        for (String excludePattern : denyUrls) {
            if (pathMatcher.match(excludePattern, uri)) {
                shouldFlag = true;
                break;
            }
        }
        return shouldFlag;
    }

    @Override
    public Object run() {
        throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.URL_DENY);
    }
}
