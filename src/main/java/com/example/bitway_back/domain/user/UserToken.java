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
public class UserToken {

    @Id
    @Column(nullable = false, length = 36)
    private String uuid;

    @Column(nullable = false, length = 300)
    private String refreshToken;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Builder
    public UserToken(String uuid, String refreshToken) {
        this.uuid = uuid;
        this.refreshToken = refreshToken;
    }
}
