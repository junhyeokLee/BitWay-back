package com.example.bitway_back.service.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                    .map(item -> Map.entry(
                            item.get("symbol").replace("USDT", ""),
                            Double.parseDouble(item.get("price"))
                    ))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            java.util.LinkedHashMap::new
                    ));
        } catch (Exception e) {
            return Map.of();
        }
    }


    @Override
    public double getPriceKrw(String symbol) {
        throw new UnsupportedOperationException("Binance는 KRW 가격 미지원");
    }

    public double getPriceChangePercent24h(String symbol) {
        String pair = symbol.toUpperCase() + "USDT";
        String url = "https://api.binance.com/api/v3/ticker/24hr?symbol=" + pair;
        try {
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            return Double.parseDouble(response.get("priceChangePercent").toString());
        } catch (Exception e) {
        }
        return -1;
    }

    public double getVolume24h(String symbol) {
        String pair = symbol.toUpperCase() + "USDT";
        String url = "https://api.binance.com/api/v3/ticker/24hr?symbol=" + pair;
        try {
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            return Double.parseDouble(response.get("quoteVolume").toString());
        } catch (Exception e) {
        }
        return -1;
    }

    public double getVolatilityIndex(String symbol) {
        String pair = symbol.toUpperCase() + "USDT";
        String url = "https://api.binance.com/api/v3/ticker/24hr?symbol=" + pair;
        try {
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            double high = Double.parseDouble(response.get("highPrice").toString());
            double low = Double.parseDouble(response.get("lowPrice").toString());
            double open = Double.parseDouble(response.get("openPrice").toString());
            return (high - low) / open * 100;
        } catch (Exception e) {
        }
        return -1;
    }

}
