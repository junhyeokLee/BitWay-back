package com.example.bitway_back.dto.request;
import lombok.Data;


@Data
public class UserRegisterReqDto {
    private String email;
    private String password;
    private String phoneNumber;
    private String nickname;
}