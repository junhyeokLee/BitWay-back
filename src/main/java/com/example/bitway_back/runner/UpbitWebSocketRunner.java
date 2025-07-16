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

        Thread clientThread = new Thread(upbitWebSocketClient::connect, "UpbitWS");
        clientThread.setDaemon(true); // 백그라운드 스레드
        clientThread.start();

        log.info("Upbit WebSocket connection started");

        // ✅ 반드시 메인 스레드가 종료되지 않도록 유지
        while (true) {
            try {
                Thread.sleep(60 * 1000); // 1분마다 자고 무한 루프
            } catch (InterruptedException e) {
                log.error("Main thread interrupted", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}