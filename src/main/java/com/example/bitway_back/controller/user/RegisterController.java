package com.example.bitway_back.controller.user;

import com.example.bitway_back.service.user.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/register")
@Tag(name = "RegisterController", description = "가입 컨트롤러")
public class RegisterController {

    private final EmailVerificationService service;

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
}