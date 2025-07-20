package com.example.bitway_back.service.exchange;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service("binance")
public class BinancePriceService implements ExchangePriceService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public double getPriceUsd(String symbol) {
        String pair = symbol.toUpperCase() + "USDT";  // ex: BTCUSDT
        String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + pair;

        try {
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.get("price") != null) {
                return Double.parseDouble(response.get("price").toString());
            }
        } catch (Exception e) {
            System.out.println("🔥 Binance 호출 실패: " + e.getMessage());
        }

        return -1; // 실패 시 -1 반환
    }

    @Override
    public double getPriceKrw(String symbol) {
        throw new UnsupportedOperationException("Binance는 KRW 가격 미지원");
    }
}

