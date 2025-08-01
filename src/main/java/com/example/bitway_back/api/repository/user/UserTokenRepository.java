package com.example.bitway_back.api.repository.user;

import com.example.bitway_back.domain.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, String> {
    Optional<UserToken> findByRefreshToken(String refreshToken);
    void deleteByUserId(Long userId);
    List<UserToken> findAllByExpiryDateBefore(LocalDateTime time);
}