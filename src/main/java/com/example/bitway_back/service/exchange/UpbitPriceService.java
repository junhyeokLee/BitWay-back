package com.example.bitway_back.service.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public Map<String, Double> getAllPricesKrw() {
        try {
            String marketsUrl = "https://api.upbit.com/v1/market/all?isDetails=false";
            List<Map<String, Object>> markets = restTemplate.getForObject(marketsUrl, List.class);
            List<String> krwMarkets = markets.stream()
                .map(m -> (String) m.get("market"))
                .filter(market -> market.startsWith("KRW-"))
                .collect(Collectors.toList());

            if (krwMarkets.isEmpty()) return Map.of();

            String tickersUrl = "https://api.upbit.com/v1/ticker?markets=" + String.join(",", krwMarkets);
            List<Map<String, Object>> tickers = restTemplate.getForObject(tickersUrl, List.class);

            return tickers.stream().collect(Collectors.toMap(
                t -> ((String) t.get("market")).replace("KRW-", ""),
                t -> ((Number) t.get("trade_price")).doubleValue()
            ));
        } catch (Exception e) {
            logger.error("[Upbit] 전체 가격 조회 실패: {}", e.getMessage());
            return Map.of();
        }
    }

    @Override
    public Map<String, Double> getAllPricesUsd() {
        throw new UnsupportedOperationException("Upbit는 USD 가격을 지원하지 않습니다.");
    }

}
