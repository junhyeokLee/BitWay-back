package com.example.bitway_back.redis;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradePublisher {

    private final StringRedisTemplate redisTemplate;
    private static final String CHANNEL_PREFIX = "trade:";

//    public void publish(String symbol, BinanceAggTradeResDto trade) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            String json = mapper.writeValueAsString(trade);
//            redisTemplate.convertAndSend(CHANNEL_PREFIX + symbol.toLowerCase(), json);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 예외 처리 로직 추가 (예: 로깅, 알림 등)
//        }
//    }

    public void publish(String symbol, String message) {
        try {
            redisTemplate.convertAndSend(CHANNEL_PREFIX + symbol.toLowerCase(), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}