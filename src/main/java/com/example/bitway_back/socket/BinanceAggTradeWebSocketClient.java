package com.example.bitway_back.socket;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import com.example.bitway_back.service.market.TradeAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BinanceAggTradeWebSocketClient {

    private static final String[] SYMBOLS = {"btcusdt"};

    @PostConstruct
    public void start() {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        for (String symbol : SYMBOLS) {
            String url = "wss://stream.binance.com:9443/ws/" + symbol + "@aggTrade";
            Request request = new Request.Builder().url(url).build();
            client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    try {
                        BinanceAggTradeResDto trade = mapper.readValue(text, BinanceAggTradeResDto.class);
                        tradeAnalysisService.processTrade(trade);
                    } catch (Exception e) {
                        log.error("WebSocket Parse Error (" + symbol + "): " + e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                    log.error("WebSocket 연결 실패: {}", t.getMessage(), t);
                    reconnect(symbol);
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    log.warn("WebSocket 종료 중... code: {}, reason: {}", code, reason);
                    webSocket.close(1000, null);
                    reconnect(symbol);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    log.warn("WebSocket 종료 완료. code: {}, reason: {}", code, reason);
                    reconnect(symbol);
                }
            });
        }
    }

    private BinanceAggTradeResDto parseTrade(String text) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(text, BinanceAggTradeResDto.class);
        } catch (Exception e) {
            log.error("WebSocket Parse Error", e);
            return null;
        }
    }

    private void reconnect(String symbol) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {}
        log.info("🔁 WebSocket 재연결 시도: {}", symbol);
        String url = "wss://stream.binance.com:9443/ws/" + symbol + "@aggTrade";
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    BinanceAggTradeResDto trade = new ObjectMapper().readValue(text, BinanceAggTradeResDto.class);
                    tradeAnalysisService.processTrade(trade);
                } catch (Exception e) {
                    log.error("WebSocket Parse Error (reconnect): {}", e.getMessage(), e);
                }
            }
        });
    }

    @Autowired
    private TradeAnalysisService tradeAnalysisService;
}