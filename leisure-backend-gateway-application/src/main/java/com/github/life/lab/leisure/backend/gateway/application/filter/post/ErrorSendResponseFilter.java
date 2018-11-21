package com.github.life.lab.leisure.backend.gateway.application.filter.post;

import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * ErrorSendResponseFilter
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
@Component
public class ErrorSendResponseFilter extends SendResponseFilter {

    public ErrorSendResponseFilter(ZuulProperties zuulProperties) {
        super(zuulProperties);
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        return Objects.nonNull(context.getThrowable())
                && (ObjectUtils.anyNotNull(
                context.getResponseDataStream(),
                context.getZuulResponseHeaders(),
                context.getResponseBody()));
    }
}