package com.example.bitway_back.controller;

import com.example.bitway_back.domain.User;
import com.example.bitway_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public User register(@RequestParam String uuid) {
        return userService.registerIfNotExist(uuid);
    }
}