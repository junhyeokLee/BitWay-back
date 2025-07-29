package com.example.bitway_back.controller.user;

import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.domain.user.UserToken;
import com.example.bitway_back.dto.request.RefreshTokenReqDto;
import com.example.bitway_back.dto.request.UserLoginReqDto;
import com.example.bitway_back.dto.request.UserRegisterReqDto;
import com.example.bitway_back.dto.response.TokenResDto;
import com.example.bitway_back.dto.response.UserLoginResDto;
import com.example.bitway_back.repository.user.UserTokenRepository;
import com.example.bitway_back.security.JwtUtil;
import com.example.bitway_back.service.user.EmailVerificationService;
import com.example.bitway_back.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "사용자 컨트롤러")
public class UserController {

    private final EmailVerificationService emailVerificationservice;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserTokenRepository userTokenRepository;

    // 회원가입
    @Operation(summary = "사용자 생성 API")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterReqDto request) {
        userService.register(request);
        return ResponseEntity.ok("회원가입 완료");
    }

    // 로그인 관련
    @Operation(summary = "로그인 API")
    @PostMapping("/login")
    public ResponseEntity<UserLoginResDto> login(@RequestBody UserLoginReqDto request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // 인증관련
    @Operation(summary = "이메일 인증 코드 전송 API")
    @PostMapping("email/send")
    public ResponseEntity<Void> send(@RequestParam String email) {
        emailVerificationservice.sendVerificationCode(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증 코드 검증 API")
    @PostMapping("email/verify")
    public ResponseEntity<Boolean> verify(@RequestParam String email, @RequestParam String code) {
        boolean result = emailVerificationservice.verifyCode(email, code);
        return ResponseEntity.ok(result);
    }


    // 조회 관련
    @Operation(summary = "모든 사용자 조회 API")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @Operation(summary = "사용자 조회 API")
    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    }

    @Operation(summary = "로그아웃 API")
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenReqDto request) {
        userService.logout(request.getRefreshToken());
        return ResponseEntity.ok("로그아웃 성공");
    }

    @Operation(summary = "리프레시 토큰 API")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResDto> refresh(@RequestBody RefreshTokenReqDto request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("RefreshToken 유효하지 않음");
        }
        UserToken token = userTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 토큰"));

        String email = jwtUtil.getSubjectFromToken(refreshToken);
        List<String> roles = List.of("ROLE_USER");

        String newAccessToken = jwtUtil.createAccessToken(email, roles);
        String newRefreshToken = jwtUtil.createRefreshToken(email, roles, String.valueOf(token.getUserId()));

        // Update refresh token in DB
        token.setRefreshToken(newRefreshToken);
        token.setExpiryDate(jwtUtil.getRefreshTokenExpiryDate());
        userTokenRepository.save(token);

        return ResponseEntity.ok(new TokenResDto(newAccessToken, newRefreshToken));
    }

    @Operation(summary = "만료된 토큰 일괄 삭제 API (관리자용)")
    @DeleteMapping("/auth/tokens/expired")
    public ResponseEntity<String> deleteExpiredTokens() {
        userService.deleteExpiredTokens();
        return ResponseEntity.ok("만료된 토큰 삭제 완료");
    }

}