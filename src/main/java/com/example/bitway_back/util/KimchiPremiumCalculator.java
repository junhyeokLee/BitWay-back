package com.example.bitway_back.util;

import com.example.bitway_back.domain.coin.CoinLogo;
import com.example.bitway_back.dto.response.KimchiPremiumResDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KimchiPremiumCalculator {

    public static KimchiPremiumResDto calculate(
            String symbol,
            double domesticPrice,
            double overseasPriceUsd,
            double exchangeRate,
            String domesticExchange,
            String overseasExchange,
            boolean isFavorite,
            double priceChangePercent24h,
            double volume24h,
            double volatilityIndex,
            List<CoinLogo> coinLogos

    ) {
        double overseasPriceKrw = overseasPriceUsd * exchangeRate;
        double priceGap = domesticPrice - overseasPriceKrw;
        double premium = (priceGap / overseasPriceKrw) * 100;
        int sortPriority = isFavorite ? 0 : 1;

        Map<String, String> logoInfo = coinLogos.stream()
                .filter(c -> c.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .map(c -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("imageUrl", Optional.ofNullable(c.getImageUrl()).orElse(""));
                    map.put("symbolName", Optional.ofNullable(c.getSymbolName()).orElse(""));
                    return map;
                })
                .orElse(Map.of());

        return KimchiPremiumResDto.builder()
                .symbol(symbol)
                .domesticExchange(domesticExchange)
                .domesticPrice(domesticPrice)
                .overseasExchange(overseasExchange)
                .overseasPrice(overseasPriceUsd)
                .exchangeRate(exchangeRate)
                .premiumRate(premium)
                .overseasPriceInKrw(overseasPriceKrw)
                .priceGap(priceGap)
                .imageUrl(logoInfo.getOrDefault("imageUrl", ""))
                .symbolName(logoInfo.getOrDefault("symbolName", ""))
                .priceChangePercent24h(priceChangePercent24h)
                .volume24h(volume24h)
                .volatilityIndex(volatilityIndex)
                .isFavorite(isFavorite)
                .sortPriority(sortPriority)
                .build();
    }
}