package com.example.bitway_back.controller;

import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "사용자 컨트롤러")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 생성 API")
    @PostMapping
    public User createUser(@RequestParam String uuid, @RequestParam String name) {
        return userService.findOrCreate(uuid, name);
    }

    @Operation(summary = "사용자 조회 API")
    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId) {
        return userService.findById(userId).orElseThrow();
    }
}