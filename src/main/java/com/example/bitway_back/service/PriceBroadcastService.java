package com.example.bitway_back.service;

import com.example.bitway_back.handler.PriceWebSocketHandler;
import org.springframework.stereotype.Service;

@Service
public class PriceBroadcastService {
    private final PriceWebSocketHandler handler;

    public PriceBroadcastService(PriceWebSocketHandler handler) {
        this.handler = handler;
    }

    public void broadcast(String message) {
        handler.broadcast(message);
    }
}
