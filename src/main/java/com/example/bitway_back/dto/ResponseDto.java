package com.example.bitway_back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class ResponseDto<T> {
	
    int status;

    T data;

    public ResponseDto(int status) {
        this.status = status;
    }

    public ResponseDto(int status, T data) {
        this.status = status;
        this.data = data;
    }
}