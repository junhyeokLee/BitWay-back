package com.example.bitway_back.repository;

import com.example.bitway_back.domain.coin.FavoriteCoin;
import com.example.bitway_back.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteCoinRepository extends JpaRepository<FavoriteCoin, Long> {
    List<FavoriteCoin> findByUser(User user);
}