// TradeSseService.java
package com.example.bitway_back.service.market;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TradeSseService {

    // symbol 별로 SseEmitter 관리
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final TradeAnalysisService tradeAnalysisService;

    public TradeSseService(TradeAnalysisService tradeAnalysisService) {
        this.tradeAnalysisService = tradeAnalysisService;
    }

    public SseEmitter createEmitter(String symbol) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 타임아웃
        try {
            emitter.send(SseEmitter.event().name("connected").data("connected"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        emitters.computeIfAbsent(symbol, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> emitters.getOrDefault(symbol, new ArrayList<>()).remove(emitter));
        emitter.onTimeout(() -> {
            emitters.getOrDefault(symbol, new ArrayList<>()).remove(emitter);
            emitter.completeWithError(new RuntimeException("SSE emitter timeout"));
        });
        emitter.onError((e) -> {
            emitters.getOrDefault(symbol, new ArrayList<>()).remove(emitter);
            emitter.completeWithError(e);
        });

        return emitter;
    }

    @Scheduled(fixedRate = 1000)
    public void sendSummaryToEmitters() {
        emitters.forEach((symbol, emitterList) -> {
            log.info("Sending SSE summary to symbol: {}", symbol);
            try {
                Map<Integer, Long> levelCounts = tradeAnalysisService.getTodaySymbolTradeLevelCounts(symbol);
                long whaleBuy = tradeAnalysisService.getWhaleBuyCount(symbol);
                long whaleSell = tradeAnalysisService.getWhaleSellCount(symbol);
                boolean volatility = tradeAnalysisService.hasRecentVolatility(symbol, 100_000);

                Map<String, Object> summary = new HashMap<>();
                summary.put("symbol", symbol);
                summary.put("levelCounts", levelCounts);
                summary.put("whaleBuyCount", whaleBuy);
                summary.put("whaleSellCount", whaleSell);
                summary.put("volatility", volatility);

                emitterList.forEach(emitter -> {
                    java.util.concurrent.CompletableFuture.runAsync(() -> {
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("summary")
                                    .data(summary));
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                            emitters.getOrDefault(symbol, new ArrayList<>()).remove(emitter);
                        }
                    });
                });
            } catch (Exception e) {
                log.warn("SSE 전송 오류: {}", e.getMessage());
            }
        });
    }

    public void broadcastToEmitters(String symbol, BinanceAggTradeResDto trade) {
        List<SseEmitter> emitterList = emitters.getOrDefault(symbol, List.of());

        emitterList.forEach(emitter -> {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("realtime")
                            .data(trade));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    emitters.getOrDefault(symbol, new ArrayList<>()).remove(emitter);
                }
            });
        });
    }
}