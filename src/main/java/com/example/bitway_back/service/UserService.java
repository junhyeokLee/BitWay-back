package com.example.bitway_back.service;

import com.example.bitway_back.domain.User;
import com.example.bitway_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User registerIfNotExist(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseGet(() -> userRepository.save(User.builder().uuid(uuid).build()));
    }
}