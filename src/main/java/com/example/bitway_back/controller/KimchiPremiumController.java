package com.example.bitway_back.controller;

import com.example.bitway_back.dto.KimchiPremiumDto;
import com.example.bitway_back.service.KimchiPremiumService;
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
@RequestMapping("/api/premium")
@RequiredArgsConstructor
@Tag(name = "KimchiPremiumController", description = "김치 프리미엄 컨트롤러")
public class KimchiPremiumController {

    private final KimchiPremiumService kimchiPremiumService;

    @Operation(summary = "김치 프리미엄 비교 API")
    @GetMapping("/kimp")
    public ResponseEntity<KimchiPremiumDto> compareKimp(
            @RequestParam String symbol,
            @RequestParam String domestic,
            @RequestParam String overseas
    ) {
        KimchiPremiumDto result = kimchiPremiumService.compare(symbol.toUpperCase(),domestic, overseas);
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "모든 김치 프리미엄 조회 API")
    @GetMapping("/kimp/all")
    public ResponseEntity<List<KimchiPremiumDto>> getAllKimp(
            @RequestParam String domestic,
            @RequestParam String overseas,
            @RequestParam(defaultValue = "price") String sortBy
    ) {
        List<KimchiPremiumDto> premiums = kimchiPremiumService.getAllPremiums(domestic, overseas,sortBy);
        return ResponseEntity.ok(premiums);
    }
}

