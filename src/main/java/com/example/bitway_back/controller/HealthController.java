package com.example.bitway_back.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("OK");
    }

    @GetMapping("/")
    public Mono<String> root() {
        return Mono.just("BitWay 서버 실행 중");
    }
}
