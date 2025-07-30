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
        emitters.computeIfAbsent(symbol, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> emitters.get(symbol).remove(emitter));
        emitter.onTimeout(() -> emitters.get(symbol).remove(emitter));
        emitter.onError((e) -> emitters.get(symbol).remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("SSE 연결 완료"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void sendTradeUpdate(String symbol, BinanceAggTradeResDto trade) {
        log.info("[SSE] Preparing to send trade update for symbol: {}", symbol);
        List<SseEmitter> emitterList = emitters.getOrDefault(symbol, List.of());
        log.info("[SSE] Sending trade update to {} emitters", emitterList.size());
        for (SseEmitter emitter : emitterList) {
            try {
                emitter.send(SseEmitter.event()
                        .name("trade")
                        .data(trade));
            } catch (IOException e) {
                emitter.completeWithError(e);
                // removed emitter during cleanup
            }
        }
    }

    // 🧹 Emitter 누수 방지를 위한 정리 로직 (1분마다)
    @Scheduled(fixedRate = 60_000)
    public void cleanInactiveEmitters() {
        emitters.forEach((symbol, list) -> {
            int before = list.size();
            list.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event().name("ping").data("keepalive"));
                    return false;
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    return true;
                }
            });
            int after = list.size();
            if (before != after) {
                log.debug("[SSE] Removed {} inactive emitters for {}", (before - after), symbol);
            }
        });
    }
}