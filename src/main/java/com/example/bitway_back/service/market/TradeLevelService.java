package com.example.bitway_back.service.market;

import com.example.bitway_back.dto.response.BinanceAggTradeResDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TradeLevelService {

    private final Map<String, List<BinanceAggTradeResDto>> tradeBufferMap = new ConcurrentHashMap<>();

    public void addTrade(String symbol, BinanceAggTradeResDto trade) {
        tradeBufferMap.computeIfAbsent(symbol, k -> new CopyOnWriteArrayList<>()).add(trade);
    }

    public List<BinanceAggTradeResDto> getRecentTrades(String symbol) {
        return tradeBufferMap.getOrDefault(symbol, List.of());
    }
}