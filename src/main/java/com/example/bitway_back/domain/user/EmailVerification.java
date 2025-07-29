package com.example.bitway_back.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification {

    @Id
    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private boolean verified;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public EmailVerification(String email, String code, boolean verified) {
        this.email = email;
        this.code = code;
        this.verified = verified;
    }

    public void verify() {
        this.verified = true;
    }
}