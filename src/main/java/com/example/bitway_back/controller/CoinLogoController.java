package com.example.bitway_back.controller;

import com.example.bitway_back.service.CoinLogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor
@Tag(name = "CoinLogoController", description = "코인 로고 컨트롤러")
public class CoinLogoController {

    private final CoinLogoService coinLogoService;

    @Operation(summary = "코인 로고 조회 API")
    @GetMapping("/logos")
    public ResponseEntity<Map<String, String>> getCoinLogos() {
        return ResponseEntity.ok(coinLogoService.getLogos());
    }

}
