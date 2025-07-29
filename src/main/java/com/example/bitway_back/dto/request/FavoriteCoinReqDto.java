package com.example.bitway_back.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class FavoriteCoinReqDto {
    private Long userId;
    private List<String> symbols;
}