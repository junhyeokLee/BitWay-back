package com.example.bitway_back.repository.user;

import com.example.bitway_back.domain.user.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
}