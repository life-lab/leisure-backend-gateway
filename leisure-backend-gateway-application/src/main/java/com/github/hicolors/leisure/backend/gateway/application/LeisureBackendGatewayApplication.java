package com.github.hicolors.leisure.backend.gateway.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ContextPathCompositeHandler;
import org.springframework.http.server.reactive.HttpHandler;

import java.util.HashMap;
import java.util.Map;

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

//    @Bean
//    public NettyReactiveWebServerFactory webServerFactory(@Value("${server.servlet.context-path:}") String contextPath) {
//        return new NettyReactiveWebServerFactory() {
//            @Override
//            public WebServer getWebServer(HttpHandler httpHandler) {
//                Map<String, HttpHandler> handlerMap = new HashMap<>();
//                handlerMap.put(contextPath, httpHandler);
//                return super.getWebServer(new ContextPathCompositeHandler(handlerMap));
//            }
//        };
//    }
}