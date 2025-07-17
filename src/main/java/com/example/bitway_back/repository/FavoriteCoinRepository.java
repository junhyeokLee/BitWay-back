package com.example.bitway_back.repository;

import com.example.bitway_back.entity.FavoriteCoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteCoinRepository extends JpaRepository<FavoriteCoin, Long> {
    List<FavoriteCoin> findByUser_Uuid(String uuid);
}
