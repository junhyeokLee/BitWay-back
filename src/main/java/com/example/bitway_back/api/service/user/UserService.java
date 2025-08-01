package com.example.bitway_back.api.service.user;

import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.domain.user.UserToken;
import com.example.bitway_back.dto.request.UserLoginReqDto;
import com.example.bitway_back.dto.request.UserRegisterReqDto;
import com.example.bitway_back.dto.response.UserLoginResDto;
import com.example.bitway_back.api.repository.user.UserRepository;
import com.example.bitway_back.api.repository.user.UserTokenRepository;
import com.example.bitway_back.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    public UserLoginResDto login(UserLoginReqDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        List<String> roles = List.of("ROLE_USER"); // 기본 권한 부여

        String email = user.getEmail();
        String accessToken = jwtUtil.createAccessToken(email, roles);
        String refreshToken = jwtUtil.createRefreshToken(email, roles, email);

        UserToken token = UserToken.builder()
            .userId(user.getId())
            .refreshToken(refreshToken)
            .expiryDate(LocalDateTime.now().plusDays(7))
            .build();
        userTokenRepository.save(token);

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

    public void logout(String refreshToken) {
        UserToken userToken = userTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 토큰입니다."));
        userTokenRepository.delete(userToken);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<UserToken> expiredTokens = userTokenRepository.findAllByExpiryDateBefore(now);
        userTokenRepository.deleteAll(expiredTokens);
    }
}