package com.github.life.lab.leisure.backend.gateway.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

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

}