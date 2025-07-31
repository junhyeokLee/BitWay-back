package com.example.bitway_back.socket;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    // 심볼별로 세션을 관리하는 구조
    private final Map<String, Set<WebSocketSession>> sessionsBySymbol = new ConcurrentHashMap<>();

    // Executor for periodic cleanup
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void startSessionCleanupTask() {
        scheduler.scheduleAtFixedRate(this::cleanClosedSessions, 10, 30, TimeUnit.SECONDS);
    }

    private void cleanClosedSessions() {
        sessionsBySymbol.forEach((symbol, sessions) -> {
            sessions.removeIf(session -> !session.isOpen());
        });
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String symbol = getSymbolFromUri(session);
        sessionsBySymbol.computeIfAbsent(symbol, s -> ConcurrentHashMap.newKeySet()).add(session);
        log.info("WebSocket 연결됨: {} -> {}", session.getId(), symbol);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionsBySymbol.values().forEach(sessions -> sessions.remove(session));
        log.info("WebSocket 연결 해제: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 클라이언트에서 메시지 수신 처리 (필요 시)
    }

    public void broadcast(String symbol, Object message) {
        Set<WebSocketSession> sessions = sessionsBySymbol.getOrDefault(symbol, Set.of());
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("메시지 직렬화 실패", e);
            return;
        }

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(json));
                } catch (Exception e) {
                    log.warn("WebSocket 전송 실패: {}", e.getMessage());
                }
            }
        }
    }

    private String getSymbolFromUri(WebSocketSession session) {
        String uri = Objects.requireNonNull(session.getUri()).toString();
        int idx = uri.indexOf("symbol=");
        return (idx != -1) ? uri.substring(idx + 7).toUpperCase() : "BTCUSDT";
    }

    public void broadcastToSessions(String symbol, BinanceAggTradeResDto trade) {
        Set<WebSocketSession> sessions = sessionsBySymbol.getOrDefault(symbol, Set.of());
        String json;
        try {
            json = objectMapper.writeValueAsString(trade);
        } catch (Exception e) {
            log.error("WebSocket 직렬화 실패", e);
            return;
        }

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(json));
                } catch (Exception e) {
                    log.warn("WebSocket 전송 실패: {}", e.getMessage());
                }
            }
        }
    }
}