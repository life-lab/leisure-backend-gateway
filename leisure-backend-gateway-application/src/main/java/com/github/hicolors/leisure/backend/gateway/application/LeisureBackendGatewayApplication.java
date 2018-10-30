package com.github.hicolors.leisure.backend.gateway.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * LeisureBackendGatewayApplication
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/10/30
 */
@SpringBootApplication
@Configuration
public class LeisureBackendGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeisureBackendGatewayApplication.class, args);
    }
}
