package com.github.hicolors.leisure.backend.gateway.application;

import com.github.hicolors.leisure.member.authorization.token.TokenStore;
import com.github.hicolors.leisure.member.authorization.token.impl.RedisTokenStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * LeisureBackendGatewayApplication
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/10/30
 */
@SpringBootApplication
@EnableFeignClients({
        "com.github.hicolors.leisure.member.authorization.feign"
})
@EnableZuulProxy
public class LeisureBackendGatewayApplication {

    @Bean
    public TokenStore redisTokenStore() {
        return new RedisTokenStore();
    }

    public static void main(String[] args) {
        SpringApplication.run(LeisureBackendGatewayApplication.class, args);
    }

}