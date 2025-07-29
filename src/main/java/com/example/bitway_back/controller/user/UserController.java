package com.example.bitway_back.controller.user;

import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.dto.request.UserRegisterReqDto;
import com.example.bitway_back.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "사용자 컨트롤러")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 생성 API")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterReqDto request) {
        userService.register(request);
        return ResponseEntity.ok("회원가입 완료");
    }

    @Operation(summary = "사용자 조회 API")
    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    }

    @Operation(summary = "모든 사용자 조회 API")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }


}