package com.example.bitway_back.service.market;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import com.example.bitway_back.redis.TradePublisher;
import com.example.bitway_back.socket.TradeWebSocketHandler;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
@RequiredArgsConstructor
public class TradeAnalysisService {

    private final TradeSseService tradeSseService;
    private final TradeWebSocketHandler tradeWebSocketHandler;
    private final TradePublisher tradePublisher;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // Add a trade to the buffer, wrapping with timestamp
    public void addTrade(String symbol, BinanceAggTradeResDto trade) {
        try {
            // 실시간 전송
            tradeWebSocketHandler.broadcastToSessions(symbol, trade);

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

    // Analyze only for trades of a given symbol
    private void analyzeIfNeeded(String symbol) {
        long now = System.currentTimeMillis();
        List<BinanceAggTradeResDto> recentTrades = getRecentTrades(symbol);
        if (!recentTrades.isEmpty()) {
            String sym = recentTrades.get(0).getSymbol();
            long startTime = recentTrades.stream().mapToLong(BinanceAggTradeResDto::getTimestamp).min().orElse(now);
            long endTime = recentTrades.stream().mapToLong(BinanceAggTradeResDto::getTimestamp).max().orElse(now);
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(java.time.ZoneId.of("Asia/Seoul"));

            double buyVolume = recentTrades.stream()
                    .filter(t -> !t.isBuyerMaker()) // 매수세
                    .mapToDouble(BinanceAggTradeResDto::getQuantity).sum();

            double sellVolume = recentTrades.stream()
                    .filter(BinanceAggTradeResDto::isBuyerMaker) // 매도세
                    .mapToDouble(BinanceAggTradeResDto::getQuantity).sum();

            Map<Integer, Long> levelCounts = recentTrades.stream()
                    .collect(Collectors.groupingBy(this::classifyTradeLevel, Collectors.counting()));

            StringBuilder log = new StringBuilder();
            log.append("코인 ").append(sym).append("\n");
            log.append("시간 ").append(formatter.format(java.time.Instant.ofEpochMilli(startTime)))
               .append(" ~ ").append(formatter.format(java.time.Instant.ofEpochMilli(endTime))).append("\n");

            log.append("[단계별 거래 수]\n");
            levelCounts.forEach((level, count) ->
                log.append(" - 단계 ").append(level).append(level == 11 ? " (고래)" : "")
                   .append(": ").append(count).append("건\n")
            );

            log.append("🐋 [고래 체결 내역]\n");
            recentTrades.stream()
                .filter(t -> classifyTradeLevel(t) == 11)
                .forEach(t -> {
                    String side = t.isBuyerMaker() ? "매도" : "매수";
                    String time = formatter.format(java.time.Instant.ofEpochMilli(t.getTimestamp()));
                    log.append(" - ").append(side)
                       .append(": 수량 ").append(t.getQuantity())
                       .append("개 × 가격 ").append(t.getPrice())
                       .append(" = 총 ").append(String.format("%.2f", t.getQuantity() * t.getPrice()))
                       .append(" (").append(time).append(")\n");
                });

            if (Math.abs(buyVolume - sellVolume) > 1000) {
                log.append("⚠️ 급변 감지! 매수-매도 차이: ")
                   .append(String.format("%.4f", Math.abs(buyVolume - sellVolume))).append("\n");
            }

            tradePublisher.publish(symbol, log.toString());

            // Store analysis log in Redis for 1 day for historical access/reconnection
            String analysisKey = "analysis:" + symbol.toLowerCase();
            try {
                redisTemplate.opsForList().rightPush(analysisKey, log.toString());
                redisTemplate.expire(analysisKey, Duration.ofDays(1));
            } catch (Exception e) {
                e.printStackTrace();
            }

//            System.out.println(log.toString());
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

}