package com.example.bitway_back.config;

import com.example.bitway_back.handler.PriceWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@EnableWebMvc
public class WebSocketConfig implements WebSocketConfigurer {

    private final PriceWebSocketHandler priceWebSocketHandler;

    public WebSocketConfig(PriceWebSocketHandler priceWebSocketHandler) {
        this.priceWebSocketHandler = priceWebSocketHandler;
    }

//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(priceWebSocketHandler, "/ws/price").setAllowedOrigins("*");
//    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(priceWebSocketHandler, "/ws")
                .setAllowedOrigins("*");
    }
}