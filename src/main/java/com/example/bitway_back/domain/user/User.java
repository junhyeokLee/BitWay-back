package com.example.bitway_back.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "bitway_user") // user → app_user 등으로 변경
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    private String userName;

    private String userDuty;

    private String authority;

    private LocalDate registrationDate;

    private String phoneNumber;

    private String password;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

}