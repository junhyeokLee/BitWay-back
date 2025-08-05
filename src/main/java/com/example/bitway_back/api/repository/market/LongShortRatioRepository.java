package com.example.bitway_back.api.repository.market;

import com.example.bitway_back.domain.market.LongShortRatio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;

public interface LongShortRatioRepository extends JpaRepository<LongShortRatio, Long> {
    Optional<LongShortRatio> findTopBySymbolOrderByTimestampDesc(String symbol);

    List<LongShortRatio> findTop24BySymbolOrderByTimestampDesc(String symbol);

    List<LongShortRatio> findTopBySymbolOrderByTimestampDesc(String symbol, Pageable pageable);

    default List<LongShortRatio> findTopNBySymbolOrderByTimestampDesc(String symbol, int limit) {
        return findTopBySymbolOrderByTimestampDesc(symbol, PageRequest.of(0, limit));
    }
}