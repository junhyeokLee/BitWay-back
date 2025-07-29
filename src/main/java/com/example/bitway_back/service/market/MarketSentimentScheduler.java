package com.example.bitway_back.service.market;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketSentimentScheduler {

    private final MarketSentimentService marketSentimentService;

    // 공포탐욕지수: 매일 자정에 한 번 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailySentimentIndex() {
        marketSentimentService.fetchAndSaveSentimentIndex();
    }

    // 롱숏비율: 1시간마다 한 번 실행
    @Scheduled(cron = "0 0 * * * *")
    public void scheduleHourlyLongShortRatio() {
        marketSentimentService.fetchAndSaveLongShortRatio("BTCUSDT");
    }


}