package com.example.bitway_back.service;

import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public User findOrCreate(String userId, String name) {
        return userRepository.findById(userId)
                .orElseGet(() -> {
                    User user = User.builder()
                            .userId(userId)
                            .userName(name)
                            .createdDateTime(LocalDateTime.now())
                            .registrationDate(LocalDate.now())
                            .build();
                    return userRepository.save(user);
                });
    }
}