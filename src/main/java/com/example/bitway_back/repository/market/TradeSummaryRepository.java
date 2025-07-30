package com.example.bitway_back.repository.market;

import com.example.bitway_back.domain.market.TradeSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface TradeSummaryRepository extends JpaRepository<TradeSummary, Long> {
    Optional<TradeSummary> findBySymbolAndTradeDate(String symbol, LocalDate tradeDate);
    List<TradeSummary> findByTradeDate(LocalDate tradeDate);

}
