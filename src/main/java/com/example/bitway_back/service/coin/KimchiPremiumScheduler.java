package com.example.bitway_back.service.coin;

import com.example.bitway_back.dto.response.KimchiPremiumDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KimchiPremiumScheduler {

    private final KimchiPremiumService kimchiPremiumService;
    private final KimchiPremiumRedisCache cache;

    @Scheduled(fixedRate = 5000) // 5초마다 캐시 업데이트
    public void updateRedisCache() {

        List<KimchiPremiumDto> list = kimchiPremiumService.getAllPremiums(null, "upbit", "binance", "kimp_desc");
        list.forEach(dto -> cache.put(dto));
        log.info("김프 캐시 갱신 완료: {}개", list.size());

//        String[] domesticExchanges = {"upbit", "bithumb"};
//        String[] overseasExchanges = {"binance", "bybit"};
//        for (String domestic : domesticExchanges) {
//            for (String overseas : overseasExchanges) {
//                List<KimchiPremiumDto> list = kimchiPremiumService.getAllPremiums(null, domestic, overseas, "kimp_desc");
//                list.forEach(dto -> cache.put(dto));
//                log.info("김프 캐시 갱신 완료: {} → {} : {}개", domestic, overseas, list.size());
//            }
//        }
    }
}