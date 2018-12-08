package com.github.life.lab.leisure.backend.gateway.sdk.authentication.processor;

import com.github.life.lab.leisure.backend.gateway.sdk.authentication.annotation.AuthorityExpression;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * AuthorityExpressionProcessor
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @since 2018-12-04
 */
@Aspect
public class AuthorityExpressionProcessor {

    @Pointcut("@annotation(com.github.life.lab.leisure.backend.gateway.sdk.authentication.annotation.AuthorityExpression)")
    public void preAuthority() {
    }

    @Around(value = "preAuthority()")
    public Object processorAroundPreAuthority(ProceedingJoinPoint joinPoint) throws Throwable {
        //todo 根据 spring el 表达式取出参数值
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        AuthorityExpression ma = method.getAnnotation(AuthorityExpression.class);
        return joinPoint.proceed();
    }

}