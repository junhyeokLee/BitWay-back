package com.example.bitway_back.service.coin;

import com.example.bitway_back.domain.coin.CoinLogo;
import com.example.bitway_back.dto.response.KimchiPremiumDto;
import com.example.bitway_back.service.exchange.ExchangePriceService;
import com.example.bitway_back.service.exchange.ExchangeRateService;
import com.example.bitway_back.util.KimchiPremiumCalculator;
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
                .map(fav -> fav.getSymbol().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet())
                : Set.of(); // 비로그인 사용자면 빈 Set

        List<KimchiPremiumDto> result = new ArrayList<>();

        for (String symbol : domesticPrices.keySet()) {
            if (!overseasPrices.containsKey(symbol)) continue;

            double domesticPrice = domesticPrices.get(symbol);
            double overseasPriceUsd = overseasPrices.get(symbol);
            boolean isFavorite = favorites.contains(symbol.toUpperCase(Locale.ROOT));

            // 새로 추가된 데이터 (priceChangePercent24h, volume24h, volatilityIndex)
            double priceChangePercent24h = overseasService.getPriceChangePercent24h(symbol);
            double volume24h = overseasService.getVolume24h(symbol);
            double volatilityIndex = overseasService.getVolatilityIndex(symbol);

            result.add(KimchiPremiumCalculator.calculate(
                    symbol,
                    domesticPrice,
                    overseasPriceUsd,
                    exchangeRate,
                    domestic,
                    overseas,
                    isFavorite,
                    priceChangePercent24h,
                    volume24h,
                    volatilityIndex,
                    coinLogos
            ));
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
