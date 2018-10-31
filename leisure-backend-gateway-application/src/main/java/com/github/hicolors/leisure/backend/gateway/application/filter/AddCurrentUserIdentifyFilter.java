package com.github.hicolors.leisure.backend.gateway.application.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * AddCurrentUserIdentifyFilter
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/10/31
 */
public class AddCurrentUserIdentifyFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return null;
    }
}