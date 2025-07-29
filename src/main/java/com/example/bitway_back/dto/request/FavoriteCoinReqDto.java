package com.example.bitway_back.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class FavoriteCoinReqDto {
    private String email;
    private List<String> symbols;
    private Boolean alertEnabled;
    private Double alertPrice;
    private Boolean enabled;
}