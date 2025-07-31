package com.example.bitway_back.controller;

import com.example.bitway_back.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisTestController {

    private final RedisPublisher redisPublisher;

    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestBody String msg) {
        redisPublisher.publish(msg);
        return ResponseEntity.ok("published: " + msg);
    }
}