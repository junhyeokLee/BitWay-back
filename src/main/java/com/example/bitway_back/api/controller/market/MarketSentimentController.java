package com.example.bitway_back.api.controller.market;


import com.example.bitway_back.api.service.market.MarketSentimentService;
import com.example.bitway_back.domain.market.LongShortRatio;
import com.example.bitway_back.domain.market.SentimentIndex;
import com.example.bitway_back.api.repository.market.LongShortRatioRepository;
import com.example.bitway_back.api.repository.market.SentimentIndexRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/market")
@Tag(name = "MarketController", description = "마켓 컨트롤러")
@RequiredArgsConstructor
public class MarketSentimentController {

    private final SentimentIndexRepository sentimentIndexRepository;
    private final LongShortRatioRepository longShortRatioRepository;
    private final MarketSentimentService marketSentimentService;

    @Autowired
    @Qualifier("sentimentIndexRedisTemplate")
    private RedisTemplate<String, SentimentIndex> sentimentIndexRedisTemplate;

    @Autowired
    @Qualifier("longShortRedisTemplate")
    private RedisTemplate<String, LongShortRatio> longShortRedisTemplate;

    // 최신 공포탐욕지수
    @Operation(summary = "최신 공포탐욕지수 조회 API")
    @GetMapping("/sentiment")
    public SentimentIndex getLatestSentimentFromCache() {
        SentimentIndex index = sentimentIndexRedisTemplate.opsForValue().get("market:sentiment");
        if (index == null) {
            marketSentimentService.fetchAndCacheSentimentIndex(); // 즉시 요청
            index = sentimentIndexRedisTemplate.opsForValue().get("market:sentiment");
            if (index == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "데이터 없음");
        }
        return index;
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

    @Operation(summary = "캐시된 롱숏 비율 조회 API")
    @GetMapping("/long-short-ratio/cache")
    public LongShortRatio getCachedLongShortRatio() {
        LongShortRatio ratio = longShortRedisTemplate.opsForValue().get("market:longshort");
        if (ratio == null) throw new RuntimeException("캐시된 데이터 없음");
        return ratio;
    }

}