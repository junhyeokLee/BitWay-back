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

    public Map<String, Double> getAllPricesKrw(boolean descending) {
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

            return tickers.stream()
                    .filter(t -> t.get("market") != null && t.get("trade_price") instanceof Number)
                    .sorted((t1, t2) -> {
                        double price1 = ((Number) t1.get("trade_price")).doubleValue();
                        double price2 = ((Number) t2.get("trade_price")).doubleValue();
                        return descending ? Double.compare(price2, price1) : Double.compare(price1, price2);
                    })
                    .collect(Collectors.toMap(
                            t -> ((String) t.get("market")).substring(4),
                            t -> ((Number) t.get("trade_price")).doubleValue(),
                            (e1, e2) -> e1,
                            java.util.LinkedHashMap::new
                    ));
        } catch (Exception e) {
            logger.error("[Upbit] 전체 가격 조회 실패: {}", e.getMessage(), e);
            return Map.of();
        }
    }

    public Map<String, Double> getAllPricesKrw() {
        return getAllPricesKrw(true);
    }

    @Override
    public Map<String, Double> getAllPricesUsd() {
        throw new UnsupportedOperationException("Upbit는 USD 가격을 지원하지 않습니다.");
    }

    public double getPriceChangePercent24h(String symbol) {
        String market = "KRW-" + symbol.toUpperCase();
        String url = "https://api.upbit.com/v1/ticker?markets=" + market;
        try {
            Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);
            if (response != null && response.length > 0 && response[0].get("signed_change_rate") instanceof Number) {
                return ((Number) response[0].get("signed_change_rate")).doubleValue() * 100;
            }
        } catch (Exception e) {
            logger.error("[Upbit] 24시간 변동률 조회 실패 (symbol={}): {}", symbol, e.getMessage());
        }
        return -1;
    }

    public double getVolume24h(String symbol) {
        String market = "KRW-" + symbol.toUpperCase();
        String url = "https://api.upbit.com/v1/ticker?markets=" + market;
        try {
            Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);
            if (response != null && response.length > 0 && response[0].get("acc_trade_price_24h") instanceof Number) {
                return ((Number) response[0].get("acc_trade_price_24h")).doubleValue();
            }
        } catch (Exception e) {
            logger.error("[Upbit] 24시간 거래대금 조회 실패 (symbol={}): {}", symbol, e.getMessage());
        }
        return -1;
    }

    public double getVolatilityIndex(String symbol) {
        String market = "KRW-" + symbol.toUpperCase();
        String url = "https://api.upbit.com/v1/ticker?markets=" + market;
        try {
            Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);
            if (response != null && response.length > 0) {
                double high = ((Number) response[0].get("high_price")).doubleValue();
                double low = ((Number) response[0].get("low_price")).doubleValue();
                double open = ((Number) response[0].get("opening_price")).doubleValue();
                return (high - low) / open * 100;
            }
        } catch (Exception e) {
            logger.error("[Upbit] 변동성 지표 계산 실패 (symbol={}): {}", symbol, e.getMessage());
        }
        return -1;
    }

}
