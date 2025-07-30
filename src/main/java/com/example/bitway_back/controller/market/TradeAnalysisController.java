package com.example.bitway_back.controller.market;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import com.example.bitway_back.service.market.TradeAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market/trade-levels")
@RequiredArgsConstructor
@Tag(name = "TradeAnalysis", description = "거래 분석 API")
public class TradeAnalysisController {

    private final TradeAnalysisService tradeAnalysisService;

    @Operation(summary = "거래 레벨 조회 API", description = "거래 레벨별 집계된 거래 데이터 조회")
    @GetMapping
    public Map<Integer, List<BinanceAggTradeResDto>> getTradeLevels() {
        return tradeAnalysisService.getTradeLevels();
    }
}
