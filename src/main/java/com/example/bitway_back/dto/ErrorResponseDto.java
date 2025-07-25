package com.example.bitway_back.dto;

import com.example.bitway_back.exception.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponseDto {

	private int status;

    private String msg;

    public ErrorResponseDto(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ErrorResponseDto(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.msg = errorCode.getMsg();
    }
}
