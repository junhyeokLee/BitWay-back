package com.example.bitway_back.repository.user;

import com.example.bitway_back.domain.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTokenRepository extends JpaRepository<UserToken, String> {
}
