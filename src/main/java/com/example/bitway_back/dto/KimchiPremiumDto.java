package com.example.bitway_back.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KimchiPremiumDto {
    private String symbol;              // 예: BTC
    private String symbolName;          // 예: 비트코인
    private String domesticExchange;    // 예: Upbit
    private double domesticPrice;       // 원화 기준
    private String overseasExchange;    // 예: Binance
    private double overseasPrice;       // USD 기준
    private double exchangeRate;        // 환율 (예: 1350)
    private double premiumRate;         // 김프 비율 (%)
    private double overseasPriceInKrw;  // 환율 적용된 해외 가격 (KRW)
    private double priceGap;            // 국내 가격과 해외 가격(KRW 환산) 차이 (원)
    private String imageUrl;              // 로고 이미지 URL
}
