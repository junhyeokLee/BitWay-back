package com.example.bitway_back.api.controller;

import com.example.bitway_back.ws.pubsub.TradeAggSubscriber;
import com.example.bitway_back.ws.handler.TradeAggWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monitor")
public class MonitorController {

    private final TradeAggWebSocketHandler webSocketHandler;
    private final TradeAggSubscriber redisMessageSubscriber;

    @GetMapping("/websocket")
    public Map<String, Object> getWebSocketStatus() {
        return Map.of(
            "activeSessions", webSocketHandler.getActiveSessionCount(),
            "redisMessages", redisMessageSubscriber.getRedisMessageCount()
        );
    }
}