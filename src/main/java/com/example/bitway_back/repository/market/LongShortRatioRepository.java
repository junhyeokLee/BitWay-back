package com.example.bitway_back.repository.market;

import com.example.bitway_back.domain.market.LongShortRatio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LongShortRatioRepository extends JpaRepository<LongShortRatio, Long> {
    Optional<LongShortRatio> findTopBySymbolOrderByTimestampDesc(String symbol);

    List<LongShortRatio> findTop24BySymbolOrderByTimestampDesc(String symbol);

    default List<LongShortRatio> findTopNBySymbolOrderByTimestampDesc(String symbol, int limit) {
        return findTop24BySymbolOrderByTimestampDesc(symbol).stream()
                .limit(limit)
                .toList();
    }
}