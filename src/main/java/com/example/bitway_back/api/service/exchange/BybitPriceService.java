package com.example.bitway_back.api.service.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Service("bybit")
public class BybitPriceService implements ExchangePriceService {

    private static final Logger logger = LoggerFactory.getLogger(BybitPriceService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    public BybitPriceService() {
    }


    @Override
    public double getPriceUsd(String symbol) {
        String url = "https://api.bybit.com/v5/market/tickers?category=spot";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
                for (Map<String, Object> item : list) {
                    // Changed matching logic to allow symbols starting with the target symbol (e.g. BTC matches BTCUSDT, BTCUSDC, etc.)
                    if (symbol != null && item.get("symbol") != null && item.get("symbol").toString().startsWith(symbol)) {
                        return Double.parseDouble(item.get("lastPrice").toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[Bybit] USD 가격 조회 실패 (symbol={}): {}", symbol, e.getMessage());
        }
        return -1;
    }


    @Override
    public double getPriceKrw(String symbol) {
        throw new UnsupportedOperationException("Bybit는 KRW 가격을 지원하지 않습니다.");
    }

    @Override
    public Map<String, Double> getAllPricesKrw() {
        throw new UnsupportedOperationException("Bybit는 KRW 가격을 지원하지 않습니다.");
    }

    @Override
    public Map<String, Double> getAllPricesUsd() {
        logger.info("[Bybit] getAllPricesUsd() 진입 확인"); // double check logger output
        String url = "https://api.bybit.com/v5/market/tickers?category=spot";
        try {
            logger.info("[Bybit] 가격 조회 요청 시작: {}", url);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            logger.info("[Bybit] 응답 수신 완료");
            logger.warn("[Bybit] 수신된 전체 응답: {}", response);
            logger.debug("[Bybit] 원시 응답: {}", response);

            if (response != null && response.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                if (result == null || !result.containsKey("list")) {
                    logger.error("[Bybit] 'list' 필드 없음 또는 result가 null: {}", result);
                    return Map.of();
                }
                List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");

                if (list == null || list.isEmpty()) {
                    logger.warn("[Bybit] 'list' 데이터가 비어있음 또는 null.");
                    return Map.of();
                }

                logger.info("[Bybit] 응답 받은 티커 수: {}", list.size());

                return list.stream()
                        .filter(t -> {
                            Object symbolObj = t.get("symbol");
                            Object priceObj = t.get("lastPrice");
                            boolean valid = symbolObj instanceof String && priceObj != null;
                            if (!valid) {
                                logger.warn("[Bybit] 잘못된 항목 필터링됨: {}", t);
                                return false;
                            }
                            String symbol = ((String) symbolObj).trim();
                            // Check for exact match ignoring case and trimming whitespace
                            return symbol != null && symbol.toUpperCase().endsWith("USDT");
                        })
                        .map(t -> {
                            String symbol = ((String) t.get("symbol")).trim();
                            String coin = symbol.substring(0, symbol.length() - 4);
                            double price;
                            try {
                                price = Double.parseDouble(t.get("lastPrice").toString().trim());
                            } catch (Exception e) {
                                logger.error("[Bybit] lastPrice 파싱 실패: {}", t.get("lastPrice"), e);
                                return null;
                            }
                            return Map.entry(coin, price);
                        })
                        .filter(java.util.Objects::nonNull)
                        .filter(e -> e.getValue() >= 0)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            } else {
                logger.error("[Bybit] 'result' 필드 없음 또는 null 응답");
            }
        } catch (Exception e) {
            logger.error("[Bybit] 전체 가격 조회 실패: {}", e.getMessage(), e);
        }
        return Map.of();
    }

    public double getPriceChangePercent24h(String symbol) {
        String url = "https://api.bybit.com/v5/market/tickers?category=spot";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
                for (Map<String, Object> item : list) {
                    if (item.get("symbol") != null && item.get("symbol").equals(symbol.toUpperCase() + "USDT")) {
                        Object pcnt = item.get("price24hPcnt");
                        if (pcnt != null) {
                            return Double.parseDouble(pcnt.toString()) * 100;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[Bybit] 24시간 변동률 조회 실패 (symbol={}): {}", symbol, e.getMessage());
        }
        return -1;
    }

    public double getVolume24h(String symbol) {
        String url = "https://api.bybit.com/v5/market/tickers?category=spot";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
                for (Map<String, Object> item : list) {
                    if (item.get("symbol") != null && item.get("symbol").equals(symbol.toUpperCase() + "USDT")) {
                        Object vol = item.get("turnover24h");
                        if (vol != null) {
                            return Double.parseDouble(vol.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[Bybit] 24시간 거래대금 조회 실패 (symbol={}): {}", symbol, e.getMessage());
        }
        return -1;
    }

    public double getVolatilityIndex(String symbol) {
        String url = "https://api.bybit.com/v5/market/tickers?category=spot";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
                for (Map<String, Object> item : list) {
                    if (item.get("symbol") != null && item.get("symbol").equals(symbol.toUpperCase() + "USDT")) {
                        Object highObj = item.get("highPrice24h");
                        Object lowObj = item.get("lowPrice24h");
                        Object openObj = item.get("openPrice");
                        if (highObj != null && lowObj != null && openObj != null) {
                            double high = Double.parseDouble(highObj.toString());
                            double low = Double.parseDouble(lowObj.toString());
                            double open = Double.parseDouble(openObj.toString());
                            return (high - low) / open * 100;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[Bybit] 변동성 계산 실패 (symbol={}): {}", symbol, e.getMessage());
        }
        return -1;
    }
}