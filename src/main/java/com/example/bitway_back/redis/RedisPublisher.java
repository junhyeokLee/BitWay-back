package com.example.bitway_back.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic topic;

    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}