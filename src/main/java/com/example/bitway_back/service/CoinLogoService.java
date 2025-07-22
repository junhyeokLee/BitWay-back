package com.example.bitway_back.service;

import com.example.bitway_back.domain.CoinLogo;
import com.example.bitway_back.repository.CoinLogoRepository;
import com.example.bitway_back.service.UpbitService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.sql.DriverManager.println;

@Service
@Transactional
public class CoinLogoService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final CoinLogoRepository coinLogoRepository;
    private final UpbitService upbitService;


    public CoinLogoService(CoinLogoRepository coinLogoRepository, UpbitService upbitService) {
        this.coinLogoRepository = coinLogoRepository;
        this.upbitService = upbitService;
    }

    // 수동으로 호출해서 로고 갱신할 때 사용 (예: Swagger에서 API 호출)
    public void refreshLogosFromApi() {
        Map<String, String> fetchedLogos = fetchFromApi();
        List<CoinLogo> existingLogos = coinLogoRepository.findAll(); // 1회 조회
        Map<String, CoinLogo> existingMap = existingLogos.stream()
                .collect(Collectors.toMap(
                        c -> c.getSymbol().toUpperCase(),
                        c -> c
                ));

        List<CoinLogo> entitiesToSave = new ArrayList<>();

        for (Map.Entry<String, String> entry : fetchedLogos.entrySet()) {
            String symbol = entry.getKey().toUpperCase();
            String url = entry.getValue();

            CoinLogo existing = existingMap.get(symbol);
            if (existing != null) {
                if (!existing.getImageUrl().equals(url)) {
                    existing.setImageUrl(url);
                    entitiesToSave.add(existing);
                }
            } else {
                entitiesToSave.add(new CoinLogo(symbol, url,null));
            }
        }

        coinLogoRepository.saveAll(entitiesToSave); // 한 번에 저장
    }

    // 로고 이름 수정 API
    public List<CoinLogo> getLogos() {
        return coinLogoRepository.findAll().stream()
                .map(logo -> new CoinLogo(
                        logo.getSymbol(),
                        logo.getImageUrl(),
                        logo.getSymbolName()
                ))
                .collect(Collectors.toList());
    }

    private Map<String, String> fetchFromApi() {
        Map<String, String> logos = new HashMap<>();
        try {
            for (int page = 0; page <= 5; page++) {
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

    public void updateCoinLogosWithUpbitSymbols() {

        List<Map<String, Object>> krwMarkets = upbitService.getKrwMarkets();
        Map<String, CoinLogo> existingMap = coinLogoRepository.findAll().stream()
                .collect(Collectors.toMap(c -> c.getSymbol().toUpperCase(), c -> c));

        List<CoinLogo> toUpdate = new ArrayList<>();

        for (Map<String, Object> market : krwMarkets) {
            String fullMarket = market.get("market").toString(); // e.g. KRW-BTC
            if (!fullMarket.startsWith("KRW-")) continue;

            String marketSymbol = fullMarket.substring(4).toUpperCase(); // BTC
            String koreanName = market.get("korean_name").toString();

            CoinLogo existing = existingMap.get(marketSymbol);
            if (existing != null) {
                existing.setSymbolName(koreanName);
                toUpdate.add(existing);
            } else {
                System.out.println("못찾음: " + marketSymbol);
            }
        }
        coinLogoRepository.saveAll(toUpdate);
    }

}