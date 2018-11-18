package com.github.hicolors.leisure.backend.gateway.application.filter.post;

import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter;
import org.springframework.stereotype.Component;

/**
 * ErrorSendResponseFilter
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
@Component
public class ErrorSendResponseFilter extends SendResponseFilter {

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        return context.getThrowable() != null
                && (!context.getZuulResponseHeaders().isEmpty()
                || context.getResponseDataStream() != null
                || context.getResponseBody() != null);
    }
}
