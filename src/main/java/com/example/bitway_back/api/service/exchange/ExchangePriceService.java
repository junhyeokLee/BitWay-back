package com.example.bitway_back.api.service.exchange;


import java.util.Map;

public interface ExchangePriceService {
    double getPriceKrw(String symbol); // 원화 기준 가격
    double getPriceUsd(String symbol); // USD 기준 가격

    Map<String, Double> getAllPricesKrw(); // e.g., Upbit에서 KRW 마켓 코인 전부
    Map<String, Double> getAllPricesUsd(); // e.g., Binance에서 USDT 마켓 코인 전부

    double getPriceChangePercent24h(String symbol); // 24시간 상승률 (%)
    double getVolume24h(String symbol); // 24시간 거래량
    double getVolatilityIndex(String symbol); // 24시간 변동성 지수
}
