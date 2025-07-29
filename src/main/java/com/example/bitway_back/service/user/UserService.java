package com.example.bitway_back.service.user;

import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.dto.request.UserLoginReqDto;
import com.example.bitway_back.dto.request.UserRegisterReqDto;
import com.example.bitway_back.dto.response.UserLoginResDto;
import com.example.bitway_back.repository.user.UserRepository;
import com.example.bitway_back.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    public UserLoginResDto login(UserLoginReqDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // Authentication authentication = authenticationManager.authenticate(
        //         new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        // );

        java.util.List<String> roles = java.util.List.of("ROLE_USER"); // 기본 권한 부여

        String email = user.getEmail();
        String accessToken = jwtUtil.createAccessToken(email, roles);
        String refreshToken = jwtUtil.createRefreshToken(email, roles, email);

        return new UserLoginResDto(accessToken, refreshToken);
    }


public void register(UserRegisterReqDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
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