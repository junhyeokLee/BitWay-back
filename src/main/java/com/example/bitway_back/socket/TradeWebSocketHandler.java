package com.example.bitway_back.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        log.info("WebSocket 연결됨: {}", sessionId);

        return session.receive()
                .doOnNext(msg -> log.info("수신 메시지 [{}]: {}", sessionId, msg.getPayloadAsText()))
                .doOnTerminate(() -> {
                    sessions.remove(sessionId);
                    log.info("WebSocket 종료됨: {}", sessionId);
                })
                .then();
    }

    public void broadcast(Object payload) {
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("WebSocket 직렬화 실패", e);
            return;
        }

        Flux.fromIterable(sessions.values())
                .filter(WebSocketSession::isOpen)
                .flatMap(session -> session.send(Mono.just(session.textMessage(json))))
                .subscribe();
    }

    public int getActiveSessionCount() {
        return (int) sessions.values().stream()
                .filter(WebSocketSession::isOpen)
                .count();
    }
}