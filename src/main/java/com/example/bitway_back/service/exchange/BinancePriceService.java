package com.example.bitway_back.service.exchange;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Map<String, Double> getAllPricesKrw() {
        throw new UnsupportedOperationException("Binance는 KRW 가격을 지원하지 않습니다.");
    }

    @Override
    public Map<String, Double> getAllPricesUsd() {
        String url = "https://api.binance.com/api/v3/ticker/price";
        try {
            List<Map<String, String>> response = restTemplate.getForObject(url, List.class);
            if (response == null) return Map.of();

            return response.stream()
                .filter(item -> item.get("symbol").endsWith("USDT"))
                .collect(Collectors.toMap(
                    item -> item.get("symbol").replace("USDT", ""),
                    item -> Double.parseDouble(item.get("price"))
                ));
        } catch (Exception e) {
            System.out.println("🔥 Binance 전체 가격 조회 실패: " + e.getMessage());
            return Map.of();
        }
    }

    @Override
    public double getPriceKrw(String symbol) {
        throw new UnsupportedOperationException("Binance는 KRW 가격 미지원");
    }
}
