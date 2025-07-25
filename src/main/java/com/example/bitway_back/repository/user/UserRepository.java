package com.example.bitway_back.repository.user;

import com.example.bitway_back.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
