package com.example.bitway_back.controller.market;


import com.example.bitway_back.domain.market.LongShortRatio;
import com.example.bitway_back.domain.market.SentimentIndex;
import com.example.bitway_back.repository.market.LongShortRatioRepository;
import com.example.bitway_back.repository.market.SentimentIndexRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
@Tag(name = "MarketController", description = "마켓 컨트롤러")
@RequiredArgsConstructor
public class MarketSentimentController {

    private final SentimentIndexRepository sentimentIndexRepository;
    private final LongShortRatioRepository longShortRatioRepository;

    // 최신 공포탐욕지수
    @Operation(summary = "최신 공포탐욕지수 조회 API")
    @GetMapping("/sentiment")
    public SentimentIndex getLatestSentiment() {
        return sentimentIndexRepository.findTopByOrderByTimestampDesc()
                .orElseThrow(() -> new RuntimeException("데이터 없음"));
    }

    // 특정 심볼의 최신 롱숏 비율
    @Operation(summary = "특정 심볼의 최신 롱숏 비율 조회 API")
    @GetMapping("/long-short-ratio")
    public LongShortRatio getLatestRatio(@RequestParam String symbol) {
        return longShortRatioRepository.findTopBySymbolOrderByTimestampDesc(symbol)
                .orElseThrow(() -> new RuntimeException("데이터 없음"));
    }

    // 최근 롱숏 비율 N개 조회 (선택사항)
    @Operation(summary = "최근 롱숏 비율 N개 조회 API")
    @GetMapping("/long-short-ratio/list")
    public List<LongShortRatio> getRecentRatios(@RequestParam String symbol,
                                                @RequestParam(defaultValue = "24") int limit) {
        return longShortRatioRepository.findTopNBySymbolOrderByTimestampDesc(symbol, limit);
    }
}