package com.example.bitway_back.socket;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import com.example.bitway_back.service.market.TradeAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class BinanceAggTradeWebSocketClient {

    private final TradeAnalysisService tradeAnalysisService;
    private final ObjectMapper objectMapper;

    private final Map<String, WebSocket> webSocketMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final String[] SYMBOLS = {"btcusdt"}; // 여기에 원하는 심볼 추가

    @PostConstruct
    public void startAll() {
        for (String symbol : SYMBOLS) {
            connect(symbol);
        }
        // 연결 상태 감시 및 재시도
        scheduler.scheduleAtFixedRate(this::checkAndReconnect, 10, 20, TimeUnit.SECONDS);
    }

    private void connect(String symbol) {
        OkHttpClient client = new OkHttpClient();
        String url = "wss://stream.binance.com:9443/ws/" + symbol.toLowerCase() + "@aggTrade";

        Request request = new Request.Builder().url(url).build();
        WebSocket ws = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    BinanceAggTradeResDto trade = objectMapper.readValue(text, BinanceAggTradeResDto.class);
                    tradeAnalysisService.processTrade(trade);
                } catch (Exception e) {
                    log.error("WebSocket Parse Error (" + symbol + "): " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                log.error("WebSocket 연결 실패 ({}) : {}", symbol, t.getMessage());
                webSocketMap.remove(symbol);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                log.warn("WebSocket 종료 중... ({}) code: {}, reason: {}", symbol, code, reason);
                webSocket.close(1000, null);
                webSocketMap.remove(symbol);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                log.warn("WebSocket 종료 완료. ({}) code: {}, reason: {}", symbol, code, reason);
                webSocketMap.remove(symbol);
            }
        });

        webSocketMap.put(symbol, ws);
        log.info("✅ WebSocket 연결 완료: {}", symbol);
    }

    private void checkAndReconnect() {
        for (String symbol : SYMBOLS) {
            WebSocket ws = webSocketMap.get(symbol);
            if (ws == null) {
                log.warn("❌ WebSocket 재연결 시도: {}", symbol);
                connect(symbol);
            }
        }
    }

    @PreDestroy
    public void shutdownAll() {
        scheduler.shutdownNow();
        webSocketMap.values().forEach(ws -> ws.close(1000, "Application shutdown"));
        webSocketMap.clear();
    }
}