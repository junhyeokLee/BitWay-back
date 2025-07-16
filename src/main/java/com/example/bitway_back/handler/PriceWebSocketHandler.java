package com.example.bitway_back.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@Component
public class PriceWebSocketHandler implements WebSocketHandler {

    // ✅ 모든 클라이언트에게 메시지를 멀티캐스트하는 Sink
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("🌐 WebSocket connected: {}", session.getId());

        // 클라이언트로부터 메시지를 받는 스트림 (선택적으로 로그만 남김)
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(msg -> log.debug("📩 Client message: {}", msg))
                .subscribe();

        // 서버에서 클라이언트로 push할 메시지 스트림
        Flux<WebSocketMessage> output = sink.asFlux()
                .map(session::textMessage);

        return session.send(output)
                .doFinally(signalType -> log.info("❌ WebSocket disconnected: {}", session.getId()));
    }

    // ✅ Upbit에서 받은 데이터를 클라이언트에 푸시하는 메서드
    public void broadcast(String message) {
        log.debug("📤 Broadcasting message to all clients");
        sink.tryEmitNext(message); // 모든 연결된 세션에 push
    }
}
