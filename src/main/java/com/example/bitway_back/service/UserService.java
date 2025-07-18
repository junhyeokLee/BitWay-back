package com.example.bitway_back.service;

import com.example.bitway_back.domain.User;
import com.example.bitway_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOrCreate(String uuid, String name) {
        return userRepository.findByUuid(uuid)
                .orElseGet(() -> userRepository.save(User.builder()
                        .uuid(uuid)
                        .name(name)
                        .build()));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}