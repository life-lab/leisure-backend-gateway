package com.github.life.lab.leisure.backend.gateway.application.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_ERROR_FILTER_ORDER;

/**
 * FilterOrder
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
public interface FilterOrder {


    /** pre **/

    /**
     * 鉴权过滤器
     */
    int DENY_FILTER_ORDER = 1;

    int AUTHENTICATION_FILTER_ORDER = 10;

    /** post **/


    /** router **/

    /**
     * error
     **/
    int ERROR_FILTER_ORDER = SEND_ERROR_FILTER_ORDER;
}
