package com.github.hicolors.leisure.backend.gateway.application.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("test")
public class TestRest {

    @GetMapping
    public Mono<String> test() {
        System.out.println(RequestContextHolder.getRequestAttributes());
        return Mono.create(t -> t.success("test"));
    }

    @GetMapping("/test")
    public Flux<String> test2() {
        System.out.println(RequestContextHolder.getRequestAttributes());
        return Flux.just("test");
    }
}
