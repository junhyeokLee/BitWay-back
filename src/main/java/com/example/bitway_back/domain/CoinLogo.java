package com.example.bitway_back.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "coin_logos")
public class CoinLogo {
    @Id
    private String symbol;

    private String imageUrl;

    private LocalDateTime updatedAt;
}
