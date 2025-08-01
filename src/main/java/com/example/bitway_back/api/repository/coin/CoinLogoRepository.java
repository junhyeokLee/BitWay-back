package com.example.bitway_back.api.repository.coin;

import com.example.bitway_back.domain.coin.CoinLogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoinLogoRepository extends JpaRepository<CoinLogo, String> {
    Optional<CoinLogo> findBySymbolIgnoreCase(String symbol);
}
