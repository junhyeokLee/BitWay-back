package com.example.bitway_back.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException{

	private int status;
    private String msg;

    public CustomException(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.msg = errorCode.getMsg();
    }
}
