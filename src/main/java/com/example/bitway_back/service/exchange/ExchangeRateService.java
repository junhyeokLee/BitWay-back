package com.example.bitway_back.service.exchange;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {

    private static final String API_KEY = "16e8aef24a9ea045aadac91e92188fe6";
    private static final String URL = "http://api.currencylayer.com/live?access_key=" + API_KEY + "&currencies=KRW&format=1";

    private final RestTemplate restTemplate = new RestTemplate();

    @Cacheable(value = "exchangeRate", key = "'usdToKrw'", cacheManager = "cacheManager")
    public double getUsdToKrwRate() {
        return 1380.0;
//        try {
//            Map<String, Object> response = restTemplate.getForObject(URL, Map.class);
//            if (response == null || !(Boolean) response.get("success")) {
//                throw new RuntimeException("환율 API 호출 실패: " + response);
//            }
//
//            Map<String, Object> quotes = (Map<String, Object>) response.get("quotes");
//            if (quotes == null || !quotes.containsKey("USDKRW")) {
//                throw new RuntimeException("USDKRW 환율 없음");
//            }
//
//            return Double.parseDouble(quotes.get("USDKRW").toString());
//        } catch (Exception e) {
//            // 1일 캐시 TTL을 고려하여 fallback 값 제공
//            System.err.println("환율 호출 실패: " + e.getMessage());
//            return 1380.0;
//        }
    }
}
