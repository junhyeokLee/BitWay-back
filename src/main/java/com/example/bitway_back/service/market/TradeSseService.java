// TradeSseService.java
package com.example.bitway_back.service.market;

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
        emitters.computeIfAbsent(symbol, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> emitters.get(symbol).remove(emitter));
        emitter.onTimeout(() -> emitters.get(symbol).remove(emitter));
        emitter.onError((e) -> emitters.get(symbol).remove(emitter));

        return emitter;
    }

    @Scheduled(fixedRate = 60_000)
    public void sendSummaryToEmitters() {
        emitters.forEach((symbol, emitterList) -> {
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
                    try {
                        emitter.send(SseEmitter.event()
                                .name("summary")
                                .data(summary));
                    } catch (IOException e) {
                        emitter.complete();
                    }
                });
            } catch (Exception e) {
                log.warn("SSE 전송 오류: {}", e.getMessage());
            }
        });
    }
}