package com.example.bitway_back.service.coin;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UpbitService {
    private final RestTemplate restTemplate = new RestTemplate();

    // KRW 마켓만 필터링
    public List<Map<String, Object>> getKrwMarkets() {
        String url = "https://api.upbit.com/v1/market/all?isDetails=false";

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> markets = response.getBody();
        return markets.stream()
                .filter(m -> m.get("market").toString().startsWith("KRW-"))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTickers(List<String> marketCodes) {
        if (marketCodes.isEmpty()) return List.of();

        String joinedMarkets = String.join(",", marketCodes); // e.g., KRW-BTC,KRW-ETH
        String url = "https://api.upbit.com/v1/ticker?markets=" + joinedMarkets;

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    public List<String> getKrwMarketCodes() {
        return getKrwMarkets().stream()
                .map(m -> m.get("market").toString())
                .collect(Collectors.toList());
    }
}
