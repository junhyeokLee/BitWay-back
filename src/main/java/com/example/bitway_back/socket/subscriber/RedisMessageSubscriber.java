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
        try {
            String topic = new String(message.getChannel());
            String symbol = topic.substring(topic.indexOf(":") + 1).toUpperCase();
            String json = new String(message.getBody());

            BinanceAggTradeResDto trade = objectMapper.readValue(json, BinanceAggTradeResDto.class);
            tradeWebSocketHandler.broadcastToSessions(symbol, trade);

        } catch (Exception e) {
            e.printStackTrace();
            // 로그를 남기거나 에러 처리를 추가할 수 있습니다.
        }
    }
}