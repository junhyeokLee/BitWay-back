package com.example.bitway_back.repository.market;

import com.example.bitway_back.domain.market.SentimentIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SentimentIndexRepository extends JpaRepository<SentimentIndex, Long> {
    Optional<SentimentIndex> findTopByOrderByTimestampDesc();
}