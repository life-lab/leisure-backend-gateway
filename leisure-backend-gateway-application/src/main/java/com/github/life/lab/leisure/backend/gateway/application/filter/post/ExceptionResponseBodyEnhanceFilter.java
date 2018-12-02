package com.github.life.lab.leisure.backend.gateway.application.filter.post;

import com.github.life.lab.leisure.backend.gateway.application.filter.FilterOrder;
import com.github.life.lab.leisure.common.model.response.ErrorResponse;
import com.github.life.lab.leisure.common.utils.JsonUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

/**
 * ExceptionResponseBodyEnhanceFilter
 *
 * @author steve
 * @since 2018-12-02
 */
@Slf4j
@Component
public class ExceptionResponseBodyEnhanceFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.EXCEPTION_RESPONSE_BODY_ENHANCE_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        boolean responseBodyFlag = false;
        boolean httpStatusFlag = false;
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletResponse response = context.getResponse();
        responseBodyFlag = ObjectUtils.anyNotNull(context.getResponseDataStream(), context.getResponseBody());
        //根据 http 状态码判断，后续需制定接口规则的标准。
        httpStatusFlag = response.getStatus() >= 400;
        return (responseBodyFlag) && (httpStatusFlag);
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();

        //获取服务响应的响应内容，因为自行处理异常放在了 responsebody 中。
        String responseBody;
        if (!StringUtils.isEmpty(context.getResponseBody())) {
            responseBody = context.getResponseBody();
        } else {
            //如果RequestContext中没有responseBody,则从响应流中读取
            try {
                InputStream responseIs = context.getResponseDataStream();
                responseBody = IOUtils.toString(responseIs, Charset.forName("UTF-8"));
                //读取完以后需要将读取的数据放入requestContext中
                context.setResponseBody(responseBody);
            } catch (Exception e) {
                log.info("getResponseDataStream error,uri:{}", context.getRequest().getRequestURI());
                return null;
            }
        }
        ErrorResponse errorResponse;
        try {
            errorResponse = JsonUtils.deserialize(responseBody, ErrorResponse.class);
            //去除 exception 信息
            errorResponse.setException(null);
            context.setResponseBody(JsonUtils.serialize(errorResponse));
        } catch (Exception e) {
            log.info("服务返回对象不是JSONObject对象,responseBody:{},requestUri:{}", responseBody, context.getRequest().getRequestURI());
            return null;
        }


        return null;
    }
}
