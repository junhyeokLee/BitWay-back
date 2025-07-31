package com.example.bitway_back.service.market;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import com.example.bitway_back.dto.response.TradeAnalysisLogResDto;
import com.example.bitway_back.dto.response.WhaleTradeResDto;
import com.example.bitway_back.redis.TradePublisher;
import com.example.bitway_back.socket.TradeWebSocketHandler;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.scheduling.annotation.Scheduled;

@Service
@RequiredArgsConstructor
public class TradeAnalysisService {

    private final TradeWebSocketHandler tradeWebSocketHandler;
    private final TradePublisher tradePublisher;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // Add a trade to the buffer, wrapping with timestamp
    public void addTrade(String symbol, BinanceAggTradeResDto trade) {
        try {
            // 실시간 전송 (비동기화 처리)
            CompletableFuture.runAsync(() -> tradeWebSocketHandler.broadcast(symbol, trade));

            // Redis에 저장 (누적 저장)
            String key = "trades:" + symbol.toLowerCase();
            String json = objectMapper.writeValueAsString(trade);
            redisTemplate.opsForList().rightPush(key, json);
            redisTemplate.opsForList().trim(key, -1000, -1);
            redisTemplate.expire(key, Duration.ofDays(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processTrade(BinanceAggTradeResDto trade) {
        if (trade == null) return;
        addTrade(trade.getSymbol(), trade); // 실시간 전송 및 Redis 저장
        analyzeIfNeeded(trade.getSymbol());
    }

    private long getTodayStartMillis() {
        return java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"))
            .withHour(8).withMinute(0).withSecond(0).withNano(0)
            .toInstant().toEpochMilli();
    }

    // Analyze only for trades of a given symbol
    private void analyzeIfNeeded(String symbol) {
        long now = System.currentTimeMillis();
        long todayStartTimestamp = getTodayStartMillis();
        List<BinanceAggTradeResDto> recentTrades = getRecentTrades(symbol).stream()
            .filter(t -> t.getTimestamp() >= todayStartTimestamp)
            .toList();
        if (!recentTrades.isEmpty()) {
            String sym = recentTrades.get(0).getSymbol();
            long endTime = recentTrades.stream().mapToLong(BinanceAggTradeResDto::getTimestamp).max().orElse(now);
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(java.time.ZoneId.of("Asia/Seoul"));

            double buyVolume = recentTrades.stream()
                    .filter(t -> !t.isBuyerMaker())
                    .mapToDouble(t -> t.getPrice() * t.getQuantity()).sum();

            double sellVolume = recentTrades.stream()
                    .filter(BinanceAggTradeResDto::isBuyerMaker)
                    .mapToDouble(t -> t.getPrice() * t.getQuantity()).sum();

            Map<Integer, Long> levelCounts = recentTrades.stream()
                    .collect(Collectors.groupingBy(this::classifyTradeLevel, Collectors.counting()));

            List<WhaleTradeResDto> whaleTrades = recentTrades.stream()
                    .filter(t -> classifyTradeLevel(t) == 11)
                    .sorted(java.util.Comparator.comparingLong(BinanceAggTradeResDto::getTimestamp))
                    .map(t -> WhaleTradeResDto.builder()
                            .side(t.isBuyerMaker() ? "매도" : "매수")
                            .quantity(t.getQuantity())
                            .price(t.getPrice())
                            .total(t.getQuantity() * t.getPrice())
                            .timestamp(formatter.format(java.time.Instant.ofEpochMilli(t.getTimestamp())))
                            .build())
                    .collect(Collectors.toList());

            TradeAnalysisLogResDto logDto = TradeAnalysisLogResDto.builder()
                    .symbol(sym)
                    .tradeLevels(levelCounts)
                    .buyVolume(buyVolume)
                    .sellVolume(sellVolume)
                    .diffVolume(Math.abs(buyVolume - sellVolume))
                    .volatilityDetected(Math.abs(buyVolume - sellVolume) > 1000)
                    .whaleTrades(whaleTrades)
                    .latestTradeTime(formatter.format(java.time.Instant.ofEpochMilli(endTime)))
                    .build();

            try {
                String json = objectMapper.writeValueAsString(logDto);

                tradePublisher.publish(symbol, json);
                tradeWebSocketHandler.broadcast(symbol, json);

                String analysisKey = "analysis:" + symbol.toLowerCase();
                redisTemplate.opsForList().rightPush(analysisKey, json);
                redisTemplate.expire(analysisKey, Duration.ofDays(1));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private int classifyTradeLevel(BinanceAggTradeResDto trade) {
        double amount = trade.getPrice() * trade.getQuantity();
        if (amount >= 100_000) return 11; // Whale
        return (int)(amount / 10_000) + 1;
    }

    public Map<Integer, List<BinanceAggTradeResDto>> getTradeLevels(String symbol) {
        return getRecentTrades(symbol).stream()
                .collect(Collectors.groupingBy(this::classifyTradeLevel));
    }

    // Return trades of given symbol
    public List<BinanceAggTradeResDto> getRecentTrades(String symbol) {
        String key = "trades:" + symbol.toLowerCase();
        List<String> rawJsonList = redisTemplate.opsForList().range(key, 0, -1);
        if (rawJsonList == null) return List.of();

        return rawJsonList.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, BinanceAggTradeResDto.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }


    public Map<Integer, Long> getTodaySymbolTradeLevelCounts(String symbol) {
        return getRecentTrades(symbol).stream()
                .collect(Collectors.groupingBy(this::classifyTradeLevel, Collectors.counting()));
    }

    public long getWhaleBuyCount(String symbol) {
        return getRecentTrades(symbol).stream()
                .filter(t -> !t.isBuyerMaker() && classifyTradeLevel(t) == 11)
                .count();
    }

    public long getWhaleSellCount(String symbol) {
        return getRecentTrades(symbol).stream()
                .filter(t -> t.isBuyerMaker() && classifyTradeLevel(t) == 11)
                .count();
    }

    public boolean hasRecentVolatility(String symbol, double thresholdUSD) {
        double buyVolume = getRecentTrades(symbol).stream()
                .filter(t -> !t.isBuyerMaker())
                .mapToDouble(t -> t.getPrice() * t.getQuantity())
                .sum();

        double sellVolume = getRecentTrades(symbol).stream()
                .filter(BinanceAggTradeResDto::isBuyerMaker)
                .mapToDouble(t -> t.getPrice() * t.getQuantity())
                .sum();

        return Math.abs(buyVolume - sellVolume) > thresholdUSD;
    }

    // Scheduled Redis cleanup at 8:00 AM Asia/Seoul
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void clearYesterdayTrades() {
        for (String symbol : List.of("btcusdt")) { // Add other symbols if needed
            redisTemplate.delete("trades:" + symbol.toLowerCase());
            redisTemplate.delete("analysis:" + symbol.toLowerCase());
        }
    }

}
