package com.example.bitway_back.dto.response;
import lombok.Data;


@Data
public class FavoriteResDto {
    private Long id;
    private String symbol;
    private Boolean alertEnabled;

    public FavoriteResDto(Long id, String symbol, Boolean alertEnabled) {
        this.id = id;
        this.symbol = symbol;
        this.alertEnabled = alertEnabled;
    }
}