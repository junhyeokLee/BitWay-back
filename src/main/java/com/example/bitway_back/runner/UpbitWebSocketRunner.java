package com.example.bitway_back.runner;

import com.example.bitway_back.websocket.UpbitWebSocketClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitWebSocketRunner implements ApplicationRunner {

    private final UpbitWebSocketClient upbitWebSocketClient;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting Upbit WebSocket connection...");
        upbitWebSocketClient.connect();

        // 메인 스레드가 종료되지 않도록 대기
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            log.error("WebSocket Runner interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
