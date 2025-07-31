package com.example.bitway_back.socket.subscriber;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import com.example.bitway_back.socket.TradeWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final TradeWebSocketHandler tradeWebSocketHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = new String(message.getChannel());
        String body = new String(message.getBody());
        String symbol;
        try {
            if (topic.startsWith("trades:")) {
                symbol = topic.substring("trades:".length()).toUpperCase();
                // Defensive JSON check: skip if not JSON object (starts with '{')
                if (body != null && body.trim().startsWith("{")) {
                    BinanceAggTradeResDto trade = objectMapper.readValue(body, BinanceAggTradeResDto.class);
                    tradeWebSocketHandler.broadcastToSessions(symbol, (Object) trade);
                } else {
                    System.err.println("trades 채널 메시지가 JSON 형식이 아님: " + body);
                }
            } else if (topic.startsWith("analysis:")) {
                symbol = topic.substring("analysis:".length()).toUpperCase();
                tradeWebSocketHandler.broadcastToSessions(symbol, (Object) body);
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
}