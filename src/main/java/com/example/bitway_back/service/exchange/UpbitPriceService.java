package com.example.bitway_back.service.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service("upbit")
public class UpbitPriceService implements ExchangePriceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(UpbitPriceService.class);

    @Override
    public double getPriceKrw(String symbol) {
        String market = "KRW-" + symbol.toUpperCase();
        String url = "https://api.upbit.com/v1/ticker?markets=" + market;

        try {
            Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);
            if (response != null && response.length > 0) {
                Object price = response[0].get("trade_price");
                if (price instanceof Number) {
                    return ((Number) price).doubleValue();
                }
            }
        } catch (Exception e) {
            logger.error("[Upbit] 가격 조회 실패 (symbol={}): {}", symbol, e.getMessage());
        }

        return -1;
    }

    @Override
    public double getPriceUsd(String symbol) {
        throw new UnsupportedOperationException("Upbit는 USD 가격을 지원하지 않습니다.");
    }
}
