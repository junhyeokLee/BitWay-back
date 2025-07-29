package com.example.bitway_back.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long EXPIRATION_MINUTES = 5;

    public void sendVerificationCode(String toEmail) {
        String code = generateCode();
        storeCode(toEmail, code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[BitWay] 이메일 인증코드 안내");
        message.setText("인증 코드: " + code + "\n5분 이내로 입력해주세요.");
        mailSender.send(message);
    }

    public boolean verifyCode(String toEmail, String code) {
        String saved = redisTemplate.opsForValue().get(toEmail);
        return saved != null && saved.equals(code);
    }

    private void storeCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(EXPIRATION_MINUTES));
    }

    private String generateCode() {
        return String.valueOf((int)((Math.random() * 900000) + 100000)); // 6자리 숫자
    }
}