package com.example.bitway_back.domain.market;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class LongShortRatio {
    @Id
    @GeneratedValue
    private Long id;
    private String symbol;
    private double longShortRatio;
    private double longAccount;
    private double shortAccount;
    private LocalDateTime timestamp;
}