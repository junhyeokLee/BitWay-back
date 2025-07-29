package com.example.bitway_back.service.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("[BitWay] 이메일 인증코드");

            String html = """
                <div style="font-family:Arial,sans-serif; padding:20px; border:1px solid #ddd; border-radius:6px;">
                  <h2><span style="color:#1A73E8;">[BitWay]</span> 이메일 인증</h2>
                  <p>아래 인증 코드를 입력해주세요. 유효 시간은 <b>5분</b>입니다.</p>
                  <div style="font-size:24px; font-weight:bold; margin-top:10px; color:#202124;">%s</div>
                  <p style="margin-top:20px; font-size:12px; color:#888;">요청하지 않았다면 이 메일을 무시해주세요.</p>
                </div>
            """.formatted(code);

            helper.setText(html, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
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