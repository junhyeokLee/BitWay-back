package com.example.bitway_back.service.user;

import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.dto.request.UserRegisterReqDto;
import com.example.bitway_back.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(UserRegisterReqDto request) {

        System.out.println("회원가입 요청 들어옴: " + request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
//                .password(request.getPassword()) // 추후 암호화 적용 필요
                .password(passwordEncoder.encode(request.getPassword()))  // 암호화 적용
                .phoneNumber(request.getPhoneNumber())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }
}