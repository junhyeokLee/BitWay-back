package com.example.bitway_back.controller;

import com.example.bitway_back.domain.coin.CoinLogo;
import com.example.bitway_back.service.CoinLogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor
@Tag(name = "CoinLogoController", description = "코인 로고 컨트롤러")
public class CoinLogoController {

    private final CoinLogoService coinLogoService;

    @Operation(summary = "코인 로고 조회 API")
    @GetMapping("/logos")
    public ResponseEntity<List<CoinLogo>> getCoinLogos() {
        return ResponseEntity.ok(coinLogoService.getLogos());
    }

    @Operation(summary = "코인 로고 갱신 API")
    @GetMapping("/logos/refresh")
    public ResponseEntity<String> refreshLogos() {
        coinLogoService.refreshLogosFromApi();
        return ResponseEntity.ok("로고가 성공적으로 갱신되었습니다.");
    }

    @Operation(summary = "코인 한글 이름 갱신 API")
    @GetMapping("/logos/refresh-symbol-names")
    public ResponseEntity<String> updateCoinLogosWithUpbitSymbols() {
        coinLogoService.updateCoinLogosWithUpbitSymbols();
        return ResponseEntity.ok("코인 한글 이름이 성공적으로 갱신되었습니다.");
    }

}
