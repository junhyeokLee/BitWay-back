package com.example.bitway_back.controller;

import com.example.bitway_back.domain.User;
import com.example.bitway_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@RequestParam String uuid, @RequestParam String name) {
        return userService.findOrCreate(uuid, name);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id).orElseThrow();
    }
}