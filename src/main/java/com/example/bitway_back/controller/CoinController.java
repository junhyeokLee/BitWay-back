package com.example.bitway_back.controller;

import com.example.bitway_back.service.UpbitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor
@Tag(name = "CoinController", description = "코인 컨트롤러")
public class CoinController {
    private final UpbitService upbitService;

    @Operation(summary = "마켓 조회 API")
    @GetMapping("/markets")
    public ResponseEntity<List<Map<String, Object>>> getMarkets() {
        List<Map<String, Object>> markets = upbitService.getKrwMarkets();
        return ResponseEntity.ok(markets);
    }

    @Operation(summary = "티커 조회 API")
    @GetMapping("/tickers")
    public ResponseEntity<List<Map<String, Object>>> getTickers(@RequestParam List<String> marketCodes) {
        List<Map<String, Object>> tickers = upbitService.getTickers(marketCodes);
        return ResponseEntity.ok(tickers);
    }

    @Operation(summary = "모든 티커 조회 API")
    @GetMapping("/tickers/all")
    public ResponseEntity<List<Map<String, Object>>> getAllTickers() {
        List<String> codes = upbitService.getKrwMarketCodes();
        List<Map<String, Object>> tickers = upbitService.getTickers(codes);
        return ResponseEntity.ok(tickers);
    }

}
