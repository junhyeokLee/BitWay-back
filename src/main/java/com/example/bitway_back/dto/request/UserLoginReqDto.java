package com.example.bitway_back.dto.request;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserLoginReqDto {
    private String email;
    private String password;
}
