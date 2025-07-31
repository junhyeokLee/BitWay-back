package com.example.bitway_back.redis.subscriber;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import com.example.bitway_back.socket.TradeWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final TradeWebSocketHandler tradeWebSocketHandler;
    private final ObjectMapper objectMapper;

    private static final AtomicLong redisMessageCount = new AtomicLong(0);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        redisMessageCount.incrementAndGet();
        String topic = new String(message.getChannel());
        String body = new String(message.getBody());
        try {
            if (topic.startsWith("trade:")) {
                // Defensive JSON check: skip if not JSON object (starts with '{')
                if (body != null && body.trim().startsWith("{")) {
                    BinanceAggTradeResDto trade = objectMapper.readValue(body, BinanceAggTradeResDto.class);
                    tradeWebSocketHandler.broadcast(trade);
                } else {
                    System.err.println("trade 채널 메시지가 JSON 형식이 아님: " + body);
                }
            } else if (topic.startsWith("analysis:")) {
                tradeWebSocketHandler.broadcast(body);
            } else {
                System.err.println("알 수 없는 Redis 채널: " + topic);
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            System.err.println("Redis 메시지 파싱 실패: " + body);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getRedisMessageCount() {
        return redisMessageCount.get();
    }
}