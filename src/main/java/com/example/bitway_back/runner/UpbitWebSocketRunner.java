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
        log.info("Upbit WebSocket connection started");

        // 메인 스레드 종료 방지
        try {
            Thread.currentThread().join(); // 무한 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Main thread interrupted", e);
        }
    }
}