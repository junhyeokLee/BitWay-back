package com.example.bitway_back.service.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("bybit")
public class BybitPriceService implements ExchangePriceService {

    private static final Logger logger = LoggerFactory.getLogger(BybitPriceService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public double getPriceKrw(String symbol) {
        throw new UnsupportedOperationException("Bybit는 KRW 가격을 지원하지 않습니다.");
    }

    @Override
    public double getPriceUsd(String symbol) {
        String url = "https://api.bybit.com/v2/public/tickers?symbol=" + symbol.toUpperCase() + "USDT";

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && "0".equals(response.get("ret_code").toString())) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) response.get("result");
                if (!result.isEmpty()) {
                    return Double.parseDouble(result.get(0).get("last_price").toString());
                }
            }
        } catch (Exception e) {
            logger.error("[Bybit] USD 가격 조회 실패 (symbol={}): {}", symbol, e.getMessage());
        }

        return -1;
    }

    @Override
    public Map<String, Double> getAllPricesKrw() {
        throw new UnsupportedOperationException("Bybit는 KRW 가격을 지원하지 않습니다.");
    }

    @Override
    public Map<String, Double> getAllPricesUsd() {
        String url = "https://api.bybit.com/v5/market/tickers?category=spot";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");

                return list.stream()
                    .filter(t -> t.get("symbol") != null && t.get("lastPrice") != null)
                    .collect(Collectors.toMap(
                        t -> ((String) t.get("symbol")).replace("USDT", ""),
                        t -> Double.parseDouble((String) t.get("lastPrice"))
                    ));
            }
        } catch (Exception e) {
            logger.error("[Bybit] 전체 가격 조회 실패: {}", e.getMessage());
        }
        return Map.of();
    }
}