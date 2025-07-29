package com.example.bitway_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResDto {
    private String accessToken;
    private String refreshToken;
}