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
public class SentimentIndex {
    @Id
    @GeneratedValue
    private Long id;
    private int value;
    private String classification;
    private LocalDateTime timestamp;
}