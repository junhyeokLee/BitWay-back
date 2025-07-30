package com.example.bitway_back.service.market;

import com.example.bitway_back.domain.market.TradeSummary;
import com.example.bitway_back.repository.market.TradeSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeSummaryScheduler {

    private final TradeAnalysisService tradeAnalysisService;
    private final TradeSummaryRepository tradeSummaryRepository;

    // 추적할 코인 목록
    private final List<String> SYMBOLS = List.of("btcusdt");

    @Scheduled(fixedRate = 10_000)
    public void summarizeRecentTrades() {
        log.info("✅ [TradeSummarySchedulerService] 1분 요약 저장 시작");
        LocalDate today = LocalDate.now();
        for (String symbol : SYMBOLS) {
            Map<Integer, Long> levelCounts = tradeAnalysisService.getTodaySymbolTradeLevelCounts(symbol);
            long whaleBuyCount = tradeAnalysisService.getWhaleBuyCount(symbol);
            long whaleSellCount = tradeAnalysisService.getWhaleSellCount(symbol);
            boolean volatility = tradeAnalysisService.hasRecentVolatility(symbol, 100_000);

            TradeSummary summary = tradeSummaryRepository.findBySymbolAndTradeDate(symbol, today)
                .orElse(TradeSummary.builder()
                    .symbol(symbol)
                    .tradeDate(today)
                    .build());

            summary.setLevel1Count(summary.getLevel1Count() + levelCounts.getOrDefault(1, 0L).intValue());
            summary.setLevel2Count(summary.getLevel2Count() + levelCounts.getOrDefault(2, 0L).intValue());
            summary.setLevel3Count(summary.getLevel3Count() + levelCounts.getOrDefault(3, 0L).intValue());
            summary.setLevel4Count(summary.getLevel4Count() + levelCounts.getOrDefault(4, 0L).intValue());
            summary.setLevel5Count(summary.getLevel5Count() + levelCounts.getOrDefault(5, 0L).intValue());
            summary.setLevel6Count(summary.getLevel6Count() + levelCounts.getOrDefault(6, 0L).intValue());
            summary.setLevel7Count(summary.getLevel7Count() + levelCounts.getOrDefault(7, 0L).intValue());
            summary.setLevel8Count(summary.getLevel8Count() + levelCounts.getOrDefault(8, 0L).intValue());
            summary.setLevel9Count(summary.getLevel9Count() + levelCounts.getOrDefault(9, 0L).intValue());
            summary.setLevel10Count(summary.getLevel10Count() + levelCounts.getOrDefault(10, 0L).intValue());
            summary.setWhaleCount(summary.getWhaleCount() + levelCounts.getOrDefault(11, 0L).intValue());
            summary.setWhaleBuyCount(summary.getWhaleBuyCount() + (int) whaleBuyCount);
            summary.setWhaleSellCount(summary.getWhaleSellCount() + (int) whaleSellCount);
            if (volatility) {
                summary.setVolatilityDetected(true);
            }

//            tradeSummaryRepository.save(summary);
            log.info("📝 [요약 누적 저장] {}: {}", symbol, summary);
        }
    }

//    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul") // 매일 오전 3시
//    public void deleteOldSummaries() {
//        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
//        List<TradeSummary> oldSummaries = tradeSummaryRepository.findByTradeDateBefore(oneYearAgo);
//        tradeSummaryRepository.deleteAll(oldSummaries);
//        log.info("🧹 [오래된 요약 삭제] {}건 제거 완료", oldSummaries.size());
//    }

    public List<TradeSummary> getTodaySummaries() {
        return tradeSummaryRepository.findByTradeDate(LocalDate.now());
    }

    public List<TradeSummary> getRecentSummaries(String symbol) {
        LocalDate today = LocalDate.now();
        if (symbol != null) {
            return tradeSummaryRepository.findBySymbolAndTradeDate(symbol.toLowerCase(), today)
                    .map(List::of) // Optional → List
                    .orElseGet(List::of); // 없으면 빈 리스트 반환
        }
        return tradeSummaryRepository.findByTradeDate(today);
    }
}