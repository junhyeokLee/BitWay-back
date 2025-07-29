package com.example.bitway_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResDto {
    private String accessToken;
    private String refreshToken; // 추가 필요
}