package com.example.bitway_back.repository.coin;

import com.example.bitway_back.domain.coin.FavoriteCoin;
import com.example.bitway_back.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FavoriteCoinRepository extends JpaRepository<FavoriteCoin, Long> {

    List<FavoriteCoin> findByUser(User user);

    void deleteByUser(User user);

    boolean existsByUserAndSymbol(User user, String symbol);

}