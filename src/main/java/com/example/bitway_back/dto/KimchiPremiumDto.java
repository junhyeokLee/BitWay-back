package com.example.bitway_back.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KimchiPremiumDto {
    private String symbol;              // 예: BTC
    private String domesticExchange;    // 예: Upbit
    private double domesticPrice;       // 원화 기준
    private String overseasExchange;    // 예: Binance
    private double overseasPrice;       // USD 기준
    private double exchangeRate;        // 환율 (예: 1350)
    private double premiumRate;         // 김프 비율 (%)
}
