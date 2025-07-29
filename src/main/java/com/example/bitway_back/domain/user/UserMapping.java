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
public class UserMapping {

    @Id
    private Long userId;

    @Column(nullable = false, length = 36)
    private String uuid;

    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Builder
    public UserMapping(Long userId, String uuid, LocalDateTime createdDateTime) {
        this.userId = userId;
        this.uuid = uuid;
        this.createdDateTime = createdDateTime;
    }
}
