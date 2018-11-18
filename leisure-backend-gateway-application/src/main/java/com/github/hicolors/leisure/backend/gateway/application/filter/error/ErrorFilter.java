package com.github.hicolors.leisure.backend.gateway.application.filter.error;

import brave.Tracer;
import com.github.hicolors.leisure.backend.gateway.application.filter.FilterOrder;
import com.github.hicolors.leisure.common.exception.ExtensionException;
import com.github.hicolors.leisure.common.exception.HttpStatus;
import com.github.hicolors.leisure.common.framework.logger.LoggerConst;
import com.github.hicolors.leisure.common.framework.springmvc.advice.enhance.event.ErrorEvent;
import com.github.hicolors.leisure.common.framework.utils.EnvHelper;
import com.github.hicolors.leisure.common.framework.utils.SpringContextUtils;
import com.github.hicolors.leisure.common.model.response.ErrorResponse;
import com.github.hicolors.leisure.common.utils.JsonUtils;
import com.github.hicolors.leisure.common.utils.Warning;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ERROR_TYPE;

/**
 * ExceptionFilter
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
@Component
@Slf4j
public class ErrorFilter extends ZuulFilter {

    @Value("${aliyun.sls.projectName:}")
    private String projectName;

    @Value("${aliyun.sls.logStoreName:}")
    private String logStoreName;

    @Autowired
    private Tracer tracer;

    @Autowired
    private EnvHelper envHelper;

    @Override
    public String filterType() {
        return ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.ERROR_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return Objects.nonNull(RequestContext.getCurrentContext().getThrowable());
    }

    private static final long UNEXPECT_EXCEPTION_CODE = 88888888L;

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Throwable e = ctx.getThrowable();

        if (Objects.nonNull(e.getCause()) && e.getCause() instanceof ExtensionException) {
            handle(ctx, (ExtensionException) e.getCause());
        } else {
            if (Objects.nonNull(e.getCause())) {
                handle(ctx, e.getCause());
            } else {
                handle(ctx, e);
            }
        }
        return null;
    }

    private void handle(RequestContext ctx, Throwable exception) {
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(UTF_8);

        ErrorResponse errorResponse = errorAttributes(exception, request, response);

        ctx.setResponseBody(JsonUtils.serialize(errorResponse));
    }

    public ErrorResponse errorAttributes(Throwable exception, HttpServletRequest request, HttpServletResponse response) {

        // 异常错误返回时 添加 trace_id 到 response header 中
        response.setHeader("trace-id", tracer.currentSpan().context().traceIdString());

        //特定异常处理所需要的参数
        Object data;
        Map paramMap = getParam(request);
        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();

        //拼装返回结果
        ErrorResponse errorResponse = new ErrorResponse(new Date(), uri);

        if (exception instanceof ExtensionException) {
            log.warn(MessageFormat.format("当前程序进入到 zuul 异常捕获器，出错的 url 为：[ {0} ]，出错的参数为：[ {1} ]", url, JsonUtils.serialize(paramMap)), exception);
            ExtensionException expectException = (ExtensionException) exception;
            errorResponse.setCode(expectException.getCode());
            errorResponse.setMessage(expectException.getMessage());
            errorResponse.setStatus(expectException.getStatus());
            data = expectException.getData();
            if (Objects.nonNull(expectException.getCause())) {
                errorResponse.setException(expectException.getCause().getMessage());
            }
        } else {
            log.error(MessageFormat.format("当前程序进入到 zuul 异常捕获器，出错的 url 为：[ {0} ]，出错的参数为：[ {1} ]", url, JsonUtils.serialize(paramMap)), exception);
            errorResponse.setCode(UNEXPECT_EXCEPTION_CODE);
            errorResponse.setMessage("服务器发生了点小故障，请联系客服人员！");
            errorResponse.setException(exception.getMessage());
            errorResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            /**
             * 参数校验异常特殊处理
             */
            if (exception instanceof NoHandlerFoundException) {
                errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            } else if (exception instanceof HttpRequestMethodNotSupportedException) {
                errorResponse.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
            } else if (exception instanceof HttpMediaTypeNotSupportedException) {
                errorResponse.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
            }
            data = new Warning(envHelper.getEnv(),
                    "服务发生非预期异常",
                    tracer.currentSpan().context().traceIdString(),
                    uri,
                    request.getMethod(),
                    JsonUtils.serialize(paramMap),
                    null,
                    new Date(),
                    exceptionMsg(exception));
        }
        response.setStatus(errorResponse.getStatus());
        SpringContextUtils.publish(new ErrorEvent(errorResponse, data));
        return errorResponse;
    }

    @SuppressWarnings("unchecked")
    private Map getParam(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>(2);
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            String requestBody = "";
            try {
                requestBody = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
                if (StringUtils.isNotBlank(requestBody)) {
                    requestBody = org.springframework.util.StringUtils.trimAllWhitespace(requestBody);
                }
            } catch (IOException ignored) {
            }
            params.put(LoggerConst.REQUEST_KEY_FORM_PARAM, JsonUtils.serialize(request.getParameterMap()));
            params.put(LoggerConst.REQUEST_KEY_BODY_PARAM, requestBody);
        }
        return params;
    }

    protected String exceptionMsg(Throwable exception) {
        if (StringUtils.isBlank(projectName) || StringUtils.isBlank(logStoreName)) {
            return exception.getMessage();
        }
        String traceId = tracer.currentSpan().context().traceIdString();
        StringBuilder url = new StringBuilder();
        url.append("https://sls.console.aliyun.com/next/project/")
                .append(projectName)
                .append("/logsearch/")
                .append(logStoreName)
                .append("?queryString=%s")
                .append("&queryTimeType=99")
                .append("&startTime=%d")
                .append("&endTime=%d");
        long startTime = new Date().toInstant().minusSeconds(10 * 60).getEpochSecond();
        long endTime = new Date().toInstant().plusSeconds(5 * 60).getEpochSecond();
        String logUrl = String.format(url.toString(), traceId, startTime, endTime);
        return exception.getMessage() + "\r\n" + "[ 更多详情请点击查看阿里云日志 ](" + logUrl + ")";
    }

}
