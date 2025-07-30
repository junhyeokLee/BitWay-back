package com.example.bitway_back.domain.market;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private LocalDate tradeDate;

    private int level1Count;
    private int level2Count;
    private int level3Count;
    private int level4Count;
    private int level5Count;
    private int level6Count;
    private int level7Count;
    private int level8Count;
    private int level9Count;
    private int level10Count;
    private int whaleCount;

    private int whaleBuyCount;
    private int whaleSellCount;

    private boolean volatilityDetected;
}