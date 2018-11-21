package com.github.life.lab.leisure.backend.gateway.application.filter.error;

import brave.Tracer;
import com.github.life.lab.leisure.backend.gateway.application.filter.FilterOrder;
import com.github.life.lab.leisure.common.exception.ExtensionException;
import com.github.life.lab.leisure.common.exception.HttpStatus;
import com.github.life.lab.leisure.common.framework.springmvc.advice.enhance.event.ErrorEvent;
import com.github.life.lab.leisure.common.framework.utils.EnvHelper;
import com.github.life.lab.leisure.common.framework.utils.SpringContextUtils;
import com.github.life.lab.leisure.common.model.response.ErrorResponse;
import com.github.life.lab.leisure.common.utils.JsonUtils;
import com.github.life.lab.leisure.common.utils.Warning;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.Date;
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

    private static final long UNEXPECT_EXCEPTION_CODE = 88888888L;

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

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Throwable e = ctx.getThrowable();
        if (Objects.nonNull(e)) {
            handle(ctx, getCause(e));
        }
        return null;
    }

    /**
     * 递归查询最底层异常
     *
     * @param cause
     * @return
     */
    private Throwable getCause(Throwable cause) {
        if (Objects.isNull(cause.getCause())) {
            return cause;
        } else {
            return getCause(cause.getCause());
        }
    }

    /**
     * 异常流程处理
     *
     * @param ctx
     * @param exception
     */
    private void handle(RequestContext ctx, Throwable exception) {
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(UTF_8);
        ErrorResponse errorResponse = errorAttributes(exception, request, response);
        ctx.setResponseBody(JsonUtils.serialize(errorResponse));
    }

    /**
     * 同 AbstractExceptionHandlerAdvice 一样对流程进行处理，部分改造 - 针对网关特定处理/文案调整
     *
     * @param exception
     * @param request
     * @param response
     * @return
     */
    public ErrorResponse errorAttributes(Throwable exception, HttpServletRequest request, HttpServletResponse response) {
        // 异常错误返回时 添加 trace_id 到 response header 中
        response.setHeader("trace-id", tracer.currentSpan().context().traceIdString());
        //特定异常处理所需要的参数
        Object data;
        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();
        //拼装返回结果
        ErrorResponse errorResponse = new ErrorResponse(new Date(), uri);
        if (exception instanceof ExtensionException) {
            log.warn(MessageFormat.format("当前路由进入到 zuul error filter 中，出错的 url 为：[ {0} ]", url), exception);

            ExtensionException expectException = (ExtensionException) exception;
            errorResponse.setCode(expectException.getCode());
            errorResponse.setMessage(expectException.getMessage());
            errorResponse.setStatus(expectException.getStatus());
            data = expectException.getData();
            if (Objects.nonNull(expectException.getCause())) {
                errorResponse.setException(expectException.getCause().getMessage());
            }
        } else {
            log.error(MessageFormat.format("当前路由进入到 zuul error filter 中，出错的 url 为：[ {0} ]", url), exception);

            errorResponse.setCode(UNEXPECT_EXCEPTION_CODE);
            errorResponse.setMessage("服务器发生了点小故障，请联系客服人员！");
            errorResponse.setException(exception.getMessage());
            errorResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            /**
             * 特殊异常特殊处理
             */
            if (exception instanceof NoHandlerFoundException) {
                errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            } else if (exception instanceof HttpRequestMethodNotSupportedException) {
                errorResponse.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
            } else if (exception instanceof HttpMediaTypeNotSupportedException) {
                errorResponse.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
            }
            data = new Warning(envHelper.getEnv(),
                    "网关路由时发生非预期异常",
                    tracer.currentSpan().context().traceIdString(),
                    uri,
                    request.getMethod(),
                    "请查看 access log.",
                    null,
                    new Date(),
                    exceptionMsg(exception));
        }
        response.setStatus(errorResponse.getStatus());
        SpringContextUtils.publish(new ErrorEvent(errorResponse, data));
        return errorResponse;
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
