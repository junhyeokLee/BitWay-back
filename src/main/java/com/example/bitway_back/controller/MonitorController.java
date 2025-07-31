package com.example.bitway_back.controller;

import com.example.bitway_back.redis.subscriber.RedisMessageSubscriber;
import com.example.bitway_back.socket.TradeWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monitor")
public class MonitorController {

    private final TradeWebSocketHandler webSocketHandler;
    private final RedisMessageSubscriber redisMessageSubscriber;

    @GetMapping("/websocket")
    public Map<String, Object> getWebSocketStatus() {
        return Map.of(
            "activeSessions", webSocketHandler.getActiveSessionCount(),
            "redisMessages", redisMessageSubscriber.getRedisMessageCount()
        );
    }
}