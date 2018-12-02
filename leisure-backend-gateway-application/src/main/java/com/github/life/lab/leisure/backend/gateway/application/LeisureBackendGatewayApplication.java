package com.github.life.lab.leisure.backend.gateway.application;

import com.github.life.lab.leisure.backend.gateway.application.filter.post.ErrorResponseFilter;
import com.github.life.lab.leisure.member.authorization.token.TokenStore;
import com.github.life.lab.leisure.member.authorization.token.impl.RedisTokenStore;
import com.github.life.lab.leisure.member.authorization.validator.MemberValidator;
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
        "com.github.life.lab.leisure.member.authorization.feign"
})
@EnableZuulProxy
public class LeisureBackendGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeisureBackendGatewayApplication.class, args);
    }

    @Bean
    public TokenStore redisTokenStore() {
        return new RedisTokenStore();
    }

    @Bean
    public MemberValidator memberValidator() {
        return new MemberValidator();
    }

    @Bean
    public ErrorResponseFilter errorResponseFilter() {
        return new ErrorResponseFilter();
    }

}