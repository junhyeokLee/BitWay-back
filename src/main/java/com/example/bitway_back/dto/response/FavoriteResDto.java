package com.example.bitway_back.dto.response;
import lombok.Data;

import java.util.List;

@Data
public class FavoriteResDto {
    private Long id;
    private String symbol;
    private Boolean alertEnabled;
    private Double alertPrice;
    private Boolean enabled;

    public FavoriteResDto(Long id, String symbol, Boolean alertEnabled, Double alertPrice, Boolean enabled) {
        this.id = id;
        this.symbol = symbol;
        this.alertEnabled = alertEnabled;
        this.alertPrice = alertPrice;
        this.enabled = enabled;
    }
}