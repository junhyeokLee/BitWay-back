package com.example.bitway_back.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KimchiPremiumResDto {
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
    private double priceChangePercent24h;  // 24시간 상승률 (%)
    private double volume24h;              // 24시간 거래량
    private double volatilityIndex;        // 변동성 지표 (%)
    private String imageUrl;              // 로고 이미지 URL
    private boolean isFavorite;
    private int sortPriority;

    public String getSymbol() {
        return symbol;
    }
    public String getSymbolName() {
        return symbolName;
    }
    public String getDomesticExchange() {
        return domesticExchange;
    }
    public double getDomesticPrice() {
        return domesticPrice;
    }
    public String getOverseasExchange() {
        return overseasExchange;
    }
    public double getOverseasPrice() {
        return overseasPrice;
    }
    public double getOverseasPriceInKrw() {
        return overseasPriceInKrw;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public double getPriceGap() {
        return priceGap;
    }

    public double getPremiumRate() {
        return premiumRate;
    }

    public int getSortPriority() {
        return sortPriority;
    }

    public double getPriceChangePercent24h() {
        return priceChangePercent24h;
    }

    public double getVolume24h() {
        return volume24h;
    }

    public double getVolatilityIndex() {
        return volatilityIndex;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

}
