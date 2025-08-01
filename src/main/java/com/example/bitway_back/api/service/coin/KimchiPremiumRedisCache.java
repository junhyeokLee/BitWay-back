package com.example.bitway_back.api.service.coin;
import com.example.bitway_back.dto.response.KimchiPremiumResDto;
import com.example.bitway_back.api.service.exchange.ExchangeRateService;
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

    private final RedisTemplate<String, KimchiPremiumResDto> redisTemplate;
    private static final String PREFIX = "kimp:";
    private final FavoriteService favoriteService;
    private final ExchangeRateService exchangeRateService;
    private final CoinLogoService coinLogoService;

    private boolean hasChanged(KimchiPremiumResDto oldVal, KimchiPremiumResDto newVal) {
        if (oldVal == null) return true;
        return !Objects.equals(oldVal.getDomesticPrice(), newVal.getDomesticPrice())
               || !Objects.equals(oldVal.getOverseasPrice(), newVal.getOverseasPrice())
               || !Objects.equals(oldVal.getExchangeRate(), newVal.getExchangeRate())
               || !Objects.equals(oldVal.getPremiumRate(), newVal.getPremiumRate())
               || !Objects.equals(oldVal.getPriceChangePercent24h(), newVal.getPriceChangePercent24h())
               || !Objects.equals(oldVal.getVolume24h(), newVal.getVolume24h())
               || !Objects.equals(oldVal.getVolatilityIndex(), newVal.getVolatilityIndex());
    }

    public void put(KimchiPremiumResDto dto) {
        String cacheKey = PREFIX + dto.getSymbol() + ":" + dto.getDomesticExchange() + ":" + dto.getOverseasExchange();
        KimchiPremiumResDto existing = redisTemplate.opsForValue().get(cacheKey);
        if (hasChanged(existing, dto)) {
            redisTemplate.opsForValue().set(cacheKey, dto, Duration.ofMinutes(5));
        }
    }

    public List<KimchiPremiumResDto> getAll() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys == null) return List.of();

        return keys.stream()
                .map(k -> redisTemplate.opsForValue().get(k))
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparingInt(KimchiPremiumResDto::getSortPriority)
                        .thenComparing(KimchiPremiumResDto::getPremiumRate, Comparator.reverseOrder())
                )
                .toList();
    }

    public List<KimchiPremiumResDto> getAllSorted(String sortBy) {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys == null) return List.of();

        return keys.stream()
                .map(k -> redisTemplate.opsForValue().get(k))
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparingInt(KimchiPremiumResDto::getSortPriority)
                        .thenComparing(sortingComparator(sortBy))
                )
                .toList();
    }

    /**
     * 거래소 이름 기준으로 필터링 및 정렬 + 사용자 관심코인, 환율, 이미지, 심볼명, priceGap, sortPriority, isFavorite 반영
     */
    public List<KimchiPremiumResDto> getAllFiltered(String userId, String domestic, String overseas, String sortBy) {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys == null) return List.of();

        // 관심 코인 Set
        Set<String> favorites = userId != null && !userId.isBlank()
                ? favoriteService.getFavorites().stream()
                .map(fav -> fav.getSymbol().toUpperCase())
                .collect(java.util.stream.Collectors.toSet())
                : java.util.Set.of();

        double exchangeRate = exchangeRateService.getUsdToKrwRate();
        var coinLogos = coinLogoService.getLogos();

        List<KimchiPremiumResDto> result = new java.util.ArrayList<>();
        for (String key : keys) {
            KimchiPremiumResDto baseDto = redisTemplate.opsForValue().get(key);
            if (baseDto == null) continue;
            if (!baseDto.getDomesticExchange().equalsIgnoreCase(domestic)
                || !baseDto.getOverseasExchange().equalsIgnoreCase(overseas)) continue;

            String symbol = baseDto.getSymbol();
            boolean isFavorite = favorites.contains(symbol.toUpperCase());
            double domesticPrice = baseDto.getDomesticPrice();
            double overseasPriceUsd = baseDto.getOverseasPrice();

            result.add(calculate(
                symbol,
                domesticPrice,
                overseasPriceUsd,
                exchangeRate,
                domestic,
                overseas,
                isFavorite,
                baseDto.getPriceChangePercent24h(),
                baseDto.getVolume24h(),
                baseDto.getVolatilityIndex(),
                coinLogos
            ));
        }

        result.sort(
                Comparator.comparing(KimchiPremiumResDto::isFavorite).reversed()
                        .thenComparingInt(KimchiPremiumResDto::getSortPriority)
                        .thenComparing(sortingComparator(sortBy))
        );

        return result;
    }

    private Comparator<KimchiPremiumResDto> sortingComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "price" -> Comparator.comparingDouble(KimchiPremiumResDto::getDomesticPrice);
            case "price_desc" -> Comparator.comparingDouble(KimchiPremiumResDto::getDomesticPrice).reversed();
            case "kimp" -> Comparator.comparingDouble(KimchiPremiumResDto::getPremiumRate);
            case "kimp_desc" -> Comparator.comparingDouble(KimchiPremiumResDto::getPremiumRate).reversed();
            default -> Comparator.comparingDouble(KimchiPremiumResDto::getDomesticPrice).reversed();
        };
    }

    public void clearAll() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
//    @PostConstruct
//    public void testRedisConnection() {
//        try {
//            String testKey = PREFIX + "test";
//            KimchiPremiumDto testDto = KimchiPremiumDto.builder()
//                    .symbol("TEST")
//                    .symbolName("테스트코인")
//                    .domesticExchange("upbit")
//                    .domesticPrice(100000)
//                    .overseasExchange("binance")
//                    .overseasPrice(70.0)
//                    .exchangeRate(1400.0)
//                    .premiumRate(1.5)
//                    .overseasPriceInKrw(98000)
//                    .priceGap(2000)
//                    .imageUrl("https://example.com/test.png")
//                    .sortPriority(1)
//                    .isFavorite(false)
//                    .build();
//
//            redisTemplate.opsForValue().set(testKey, testDto, Duration.ofMinutes(1));
//            KimchiPremiumDto retrieved = redisTemplate.opsForValue().get(testKey);
//
//            if (retrieved != null) {
//                System.out.println("✅ Redis 연결 테스트 성공: " + retrieved.getSymbolName());
//            } else {
//                System.err.println("❌ Redis 연결 실패 또는 값 조회 실패");
//            }
//        } catch (Exception e) {
//            System.err.println("❌ Redis 연결 중 예외 발생: " + e.getMessage());
//        }
//    }
}
