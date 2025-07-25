package com.example.bitway_back.service;

import com.example.bitway_back.domain.coin.CoinLogo;
import com.example.bitway_back.dto.coin.KimchiPremiumDto;
import com.example.bitway_back.service.exchange.ExchangePriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KimchiPremiumService {

    private final ExchangeRateService exchangeRateService;
    private final CoinLogoService coinLogoService;
    private final Map<String, ExchangePriceService> exchangeServices;
    private final FavoriteService favoriteService;

    public KimchiPremiumDto compare(String symbol, String domestic, String overseas) {

        ExchangePriceService domesticService = exchangeServices.get(domestic.toLowerCase(Locale.ROOT));
        ExchangePriceService overseasService = exchangeServices.get(overseas.toLowerCase(Locale.ROOT));

        if (domesticService == null || overseasService == null) {
            throw new IllegalArgumentException("지원하지 않는 거래소: " + domestic + " 또는 " + overseas);
        }

        double domesticPrice = domesticService.getPriceKrw(symbol);
        double overseasPriceUsd = overseasService.getPriceUsd(symbol);
        double exchangeRate = exchangeRateService.getUsdToKrwRate();
        List<CoinLogo> coinLogos = coinLogoService.getLogos();
        double overseasPriceKrw = overseasPriceUsd * exchangeRate;

        double premium = ((domesticPrice - overseasPriceKrw) / overseasPriceKrw) * 100;

//        Map<String, String> logoInfo = coinLogos.getOrDefault(symbol.toUpperCase(Locale.ROOT), Map.of());
        Map<String, String> logoInfo = coinLogos.stream()
                .filter(c -> c.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .map(c -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("imageUrl", c.getImageUrl());
                    map.put("symbolName", c.getSymbolName());
                    return map;
                })
                .orElse(Map.of());

        return KimchiPremiumDto.builder()
                .symbol(symbol)
                .domesticExchange(domestic)
                .domesticPrice(domesticPrice)
                .overseasExchange(overseas)
                .overseasPrice(overseasPriceUsd)
                .exchangeRate(exchangeRate)
                .premiumRate(premium)
                .overseasPriceInKrw(overseasPriceKrw)
                .priceGap(domesticPrice - overseasPriceKrw)
                .imageUrl(logoInfo.getOrDefault("imageUrl", ""))
                .symbolName(logoInfo.getOrDefault("symbolName", ""))
                .build();
    }

    public List<KimchiPremiumDto> getAllPremiums(String userId, String domestic, String overseas,String sortBy) {
        ExchangePriceService exchange = exchangeServices.get(domestic.toLowerCase(Locale.ROOT));

        if (exchange == null) {
            throw new IllegalArgumentException("지원하지 않는 거래소");
        }

        ExchangePriceService overseasService = exchangeServices.get(overseas.toLowerCase(Locale.ROOT));
        Map<String, Double> overseasPrices = overseasService.getAllPricesUsd();

        Map<String, Double> domesticPrices = exchange.getAllPricesKrw(); // e.g., BTC → 1000만 원

        double exchangeRate = exchangeRateService.getUsdToKrwRate();
        List<CoinLogo> coinLogos = coinLogoService.getLogos();

        Set<String> favorites = userId != null && !userId.isBlank()
                ? favoriteService.getFavorites().stream()
                .map(fav -> fav.getCoinCode().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet())
                : Set.of(); // 비로그인 사용자면 빈 Set

        List<KimchiPremiumDto> result = new ArrayList<>();

        for (String symbol : domesticPrices.keySet()) {
            if (!overseasPrices.containsKey(symbol)) continue;

            double domesticPrice = domesticPrices.get(symbol);
            double overseasPriceUsd = overseasPrices.get(symbol);
            double overseasPriceKrw = overseasPriceUsd * exchangeRate;

            double priceGap = domesticPrice - overseasPriceKrw;
            double premium = (priceGap / overseasPriceKrw) * 100;

            Map<String, String> logoInfo = coinLogos.stream()
                    .filter(c -> c.getSymbol().equalsIgnoreCase(symbol))
                    .findFirst()
                    .map(c -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("imageUrl", c.getImageUrl());
                        map.put("symbolName", c.getSymbolName());
                        return map;
                    })
                    .orElse(Map.of());

            boolean isFavorite = favorites.contains(symbol.toUpperCase(Locale.ROOT));
            int sortPriority = isFavorite ? 0 : 1;

            result.add(KimchiPremiumDto.builder()
                    .symbol(symbol)
                    .domesticExchange(domestic)
                    .domesticPrice(domesticPrice)
                    .overseasExchange(overseas)
                    .overseasPrice(overseasPriceUsd)
                    .exchangeRate(exchangeRate)
                    .premiumRate(premium)
                    .overseasPriceInKrw(overseasPriceKrw)
                    .priceGap(priceGap)
                    .imageUrl(logoInfo.getOrDefault("imageUrl", ""))
                    .symbolName(logoInfo.getOrDefault("symbolName", ""))
                    .isFavorite(isFavorite)
                    .sortPriority(sortPriority)
                    .build());
        }

        result.sort(Comparator
            .comparingInt(KimchiPremiumDto::getSortPriority)
            .thenComparing(sortingComparator(sortBy))
        );

        return result;
    }


    private Comparator<KimchiPremiumDto> sortingComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "price" -> Comparator.comparingDouble(KimchiPremiumDto::getDomesticPrice);
            case "price_desc" -> Comparator.comparingDouble(KimchiPremiumDto::getDomesticPrice).reversed();
            case "kimp" -> Comparator.comparingDouble(KimchiPremiumDto::getPremiumRate);
            case "kimp_desc" -> Comparator.comparingDouble(KimchiPremiumDto::getPremiumRate).reversed();
            default -> Comparator.comparingDouble(KimchiPremiumDto::getDomesticPrice).reversed();
        };
    }

}
