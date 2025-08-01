package com.example.bitway_back.api.repository.user;

import com.example.bitway_back.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email); // O

    Optional<User> findByEmail(String email);
}

