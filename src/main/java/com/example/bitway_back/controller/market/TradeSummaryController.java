package com.example.bitway_back.controller.market;

import com.example.bitway_back.domain.market.TradeSummary;
import com.example.bitway_back.service.market.TradeSummaryScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
@Tag(name = "TradeSummary", description = "거래 요약 API")
public class TradeSummaryController {

    private final TradeSummaryScheduler tradeSummaryService;

    @Operation(summary = "최근 거래 요약 조회 API")
    @GetMapping("/recent")
    public List<TradeSummary> getRecentSummaries(@RequestParam(required = false) String symbol) {
        return tradeSummaryService.getRecentSummaries(symbol);
    }

    @Operation(summary = "오늘의 거래 요약 조회 API")
    @GetMapping("/today")
    public ResponseEntity<List<TradeSummary>> getTodaySummaries() {
        List<TradeSummary> summaries = tradeSummaryService.getTodaySummaries();
        return ResponseEntity.ok(summaries);
    }
}