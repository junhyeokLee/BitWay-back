package com.example.bitway_back.exception.handler;

import com.example.bitway_back.dto.ErrorResponseDto;
import com.example.bitway_back.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.FORBIDDEN_ACCESS);
        String result = objectMapper.writeValueAsString(errorResponseDto);

        response.getWriter().write(result);
    }
}
