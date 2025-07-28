package com.example.bitway_back.service.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.stream.Collectors;

//@Service("bithumb")
public class BithumbPriceService implements ExchangePriceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(BithumbPriceService.class);

    @Override
    public double getPriceKrw(String symbol) {
        String url = "https://api.bithumb.com/public/ticker/" + symbol.toUpperCase() + "_KRW";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, String> data = (Map<String, String>) response.get("data");
            return Double.parseDouble(data.get("closing_price"));
        } catch (Exception e) {
            logger.error("[Bithumb] 가격 조회 실패 (symbol={}): {}", symbol, e.getMessage());
            return -1;
        }
    }

    @Override
    public Map<String, Double> getAllPricesKrw() {
        try {
            String url = "https://api.bithumb.com/public/ticker/ALL_KRW";
            logger.info("[Bithumb] 전체 가격 조회 요청: {}", url);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Map<String, String>> data = (Map<String, Map<String, String>>) response.get("data");

            if (data == null) {
                logger.warn("[Bithumb] data 필드가 null입니다.");
                return Map.of();
            }

            logger.info("[Bithumb] 전체 코인 수신 완료: {}개", data.size());
            logger.info("[Bithumb] 예시 데이터 (BTC): {}", data.get("BTC"));

            return data.entrySet().stream()
                    .filter(e -> !"date".equalsIgnoreCase(e.getKey()))
                    .collect(Collectors.toMap(
                            e -> e.getKey().toUpperCase(),
                            e -> Double.parseDouble(e.getValue().get("closing_price"))
                    ));
        } catch (Exception e) {
            logger.error("[Bithumb] 전체 가격 조회 실패: {}", e.getMessage());
            return Map.of();
        }
    }

    @Override
    public double getPriceUsd(String symbol) {
        throw new UnsupportedOperationException("Bithumb는 USD 가격을 지원하지 않습니다.");
    }

    @Override
    public Map<String, Double> getAllPricesUsd() {
        throw new UnsupportedOperationException("Bithumb는 USD 가격을 지원하지 않습니다.");
    }

    public double getPriceChangePercent24h(String symbol) {
        String url = "https://api.bithumb.com/public/ticker/" + symbol.toUpperCase() + "_KRW";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, String> data = (Map<String, String>) response.get("data");
            return Double.parseDouble(data.get("fluctate_rate_24H"));
        } catch (Exception e) {
            logger.error("[Bithumb] 변동률 조회 실패 (symbol={}): {}", symbol, e.getMessage());
        }
        return -1;
    }

    public double getVolume24h(String symbol) {
        String url = "https://api.bithumb.com/public/ticker/" + symbol.toUpperCase() + "_KRW";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, String> data = (Map<String, String>) response.get("data");
            return Double.parseDouble(data.get("acc_trade_value_24H"));
        } catch (Exception e) {
            logger.error("[Bithumb] 거래대금 조회 실패 (symbol={}): {}", symbol, e.getMessage());
        }
        return -1;
    }

    public double getVolatilityIndex(String symbol) {
        String url = "https://api.bithumb.com/public/ticker/" + symbol.toUpperCase() + "_KRW";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, String> data = (Map<String, String>) response.get("data");
            double high = Double.parseDouble(data.get("max_price"));
            double low = Double.parseDouble(data.get("min_price"));
            double open = Double.parseDouble(data.get("opening_price"));
            return (high - low) / open * 100;
        } catch (Exception e) {
            logger.error("[Bithumb] 변동성 계산 실패 (symbol={}): {}", symbol, e.getMessage());
        }
        return -1;
    }
}