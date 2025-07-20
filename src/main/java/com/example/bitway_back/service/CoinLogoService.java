package com.example.bitway_back.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoinLogoService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Cacheable(value = "coinLogos", key = "'all'", cacheManager = "cacheManager")
    public Map<String, String> getLogos() {
        Map<String, String> logos = new HashMap<>();
        try {
            for (int page = 1; page <= 5; page++) {
                String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=krw&order=market_cap_desc&per_page=250&page=" + page;
                ResponseEntity<List> res = restTemplate.exchange(url, HttpMethod.GET, null, List.class);

                for (Object obj : res.getBody()) {
                    Map<String, Object> coin = (Map<String, Object>) obj;
                    String symbol = (coin.get("symbol") + "").toUpperCase();
                    String image = (coin.get("image") + "");
                    if (!symbol.isEmpty() && !image.isEmpty()) {
                        logos.put(symbol, image);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logos;
    }
}
