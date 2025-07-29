package com.example.bitway_back.controller.user;

import com.example.bitway_back.dto.request.UserLoginReqDto;
import com.example.bitway_back.dto.response.UserLoginResDto;
import com.example.bitway_back.service.user.EmailVerificationService;
import com.example.bitway_back.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/register")
@Tag(name = "RegisterController", description = "가입 컨트롤러")
public class RegisterController {

    private final EmailVerificationService service;
    private final UserService userService;

    @Operation(summary = "이메일 인증 코드 전송 API")
    @PostMapping("email/send")
    public ResponseEntity<Void> send(@RequestParam String email) {
        service.sendVerificationCode(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증 코드 검증 API")
    @PostMapping("email/verify")
    public ResponseEntity<Boolean> verify(@RequestParam String email, @RequestParam String code) {
        boolean result = service.verifyCode(email, code);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "로그인 API")
    @PostMapping("/login")
    public ResponseEntity<UserLoginResDto> login(@RequestBody UserLoginReqDto request) {
        return ResponseEntity.ok(userService.login(request));
    }
}