package com.example.bitway_back.service;

import com.example.bitway_back.dto.coin.KimchiPremiumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.time.Duration;
import java.util.Comparator;

import static com.example.bitway_back.util.KimchiPremiumCalculator.calculate;

@Service
@RequiredArgsConstructor
public class KimchiPremiumRedisCache {

    private final RedisTemplate<String, KimchiPremiumDto> redisTemplate;
    private static final String PREFIX = "kimp:";
    private final FavoriteService favoriteService;
    private final ExchangeRateService exchangeRateService;
    private final CoinLogoService coinLogoService;

    private boolean hasChanged(KimchiPremiumDto oldVal, KimchiPremiumDto newVal) {
        if (oldVal == null) return true;
        return !Objects.equals(oldVal.getDomesticPrice(), newVal.getDomesticPrice()) ||
               !Objects.equals(oldVal.getOverseasPrice(), newVal.getOverseasPrice()) ||
               !Objects.equals(oldVal.getExchangeRate(), newVal.getExchangeRate()) ||
               !Objects.equals(oldVal.getPremiumRate(), newVal.getPremiumRate());
    }

    public void put(KimchiPremiumDto dto) {
        KimchiPremiumDto existing = redisTemplate.opsForValue().get(PREFIX + dto.getSymbol());
        if (hasChanged(existing, dto)) {
            redisTemplate.opsForValue().set(PREFIX + dto.getSymbol(), dto, Duration.ofMinutes(5));
        }
    }

    public List<KimchiPremiumDto> getAll() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys == null) return List.of();

        return keys.stream()
                .map(k -> redisTemplate.opsForValue().get(k))
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparingInt(KimchiPremiumDto::getSortPriority)
                        .thenComparing(KimchiPremiumDto::getPremiumRate, Comparator.reverseOrder())
                )
                .toList();
    }

    public List<KimchiPremiumDto> getAllSorted(String sortBy) {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys == null) return List.of();

        return keys.stream()
                .map(k -> redisTemplate.opsForValue().get(k))
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparingInt(KimchiPremiumDto::getSortPriority)
                        .thenComparing(sortingComparator(sortBy))
                )
                .toList();
    }

    /**
     * 거래소 이름 기준으로 필터링 및 정렬 + 사용자 관심코인, 환율, 이미지, 심볼명, priceGap, sortPriority, isFavorite 반영
     */
    public List<KimchiPremiumDto> getAllFiltered(String userId, String domestic, String overseas, String sortBy) {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys == null) return List.of();

        // 관심 코인 Set
        Set<String> favorites = userId != null && !userId.isBlank()
                ? favoriteService.getFavorites().stream()
                .map(fav -> fav.getCoinCode().toUpperCase())
                .collect(java.util.stream.Collectors.toSet())
                : java.util.Set.of();

        double exchangeRate = exchangeRateService.getUsdToKrwRate();
        var coinLogos = coinLogoService.getLogos();

        List<KimchiPremiumDto> result = new java.util.ArrayList<>();
        for (String key : keys) {
            KimchiPremiumDto baseDto = redisTemplate.opsForValue().get(key);
            if (baseDto == null) continue;
            if (!baseDto.getDomesticExchange().equalsIgnoreCase(domestic)
                || !baseDto.getOverseasExchange().equalsIgnoreCase(overseas)) continue;

            String symbol = baseDto.getSymbol();
            boolean isFavorite = favorites.contains(symbol.toUpperCase());
            double domesticPrice = baseDto.getDomesticPrice();
            double overseasPriceUsd = baseDto.getOverseasPrice();
            // Use KimchiPremiumCalculator to generate the DTO
            result.add(calculate(
                symbol,
                domesticPrice,
                overseasPriceUsd,
                exchangeRate,
                domestic,
                overseas,
                isFavorite,
                coinLogos
            ));
        }

        result.sort(
                Comparator.comparingInt(KimchiPremiumDto::getSortPriority)
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

    public void clearAll() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}