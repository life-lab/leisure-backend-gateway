package com.github.life.lab.leisure.backend.gateway.sdk.authentication.annotation;

import java.lang.annotation.*;

/**
 * AuthorityExpression（权限表达式）
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @since 2018-12-04
 */
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AuthorityExpression {

    /**
     * 类似 spring el 表达式的东东，取参数中的值用
     *
     * @return spring el 表达式
     */
    String value();

    /**
     * 角色
     *
     * @return 角色列表
     */
    String[] roles();

    /**
     * 权限策略
     *
     * @return if true permit else deny
     */
    boolean strategy() default true;
}
