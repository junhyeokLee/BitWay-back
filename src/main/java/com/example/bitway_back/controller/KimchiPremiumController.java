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
public class KimchiPremiumController {

    private final KimchiPremiumService kimchiPremiumService;

    @GetMapping("/kimp")
    public ResponseEntity<KimchiPremiumDto> compareKimp(
            @RequestParam String symbol,
            @RequestParam String domestic,
            @RequestParam String overseas
    ) {
        KimchiPremiumDto result = kimchiPremiumService.compare(symbol.toUpperCase(),domestic, overseas);
        return ResponseEntity.ok(result);
    }
}

