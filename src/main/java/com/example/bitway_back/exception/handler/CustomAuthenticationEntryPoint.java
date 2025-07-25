package com.example.bitway_back.exception.handler;

import com.example.bitway_back.dto.ErrorResponseDto;
import com.example.bitway_back.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.UNAUTHORIZED_REQUEST);
        String result = objectMapper.writeValueAsString(errorResponseDto);

        response.getWriter().write(result);
    }
}
