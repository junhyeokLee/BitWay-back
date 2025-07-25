package com.example.bitway_back.service;

import com.example.bitway_back.domain.coin.FavoriteCoin;
import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.dto.coin.FavoriteDto;
import com.example.bitway_back.exception.CustomException;
import com.example.bitway_back.exception.ErrorCode;
import com.example.bitway_back.repository.FavoriteCoinRepository;
import com.example.bitway_back.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteCoinRepository favoriteRepo;

    public List<FavoriteCoin> getFavorites() {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_REQUEST));
        return favoriteRepo.findByUser(user);
    }

    public FavoriteCoin addFavorite(FavoriteDto dto) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_REQUEST));

        FavoriteCoin favorite = new FavoriteCoin();
        favorite.setCoinCode(dto.getCoinCode());
        favorite.setCoinName(dto.getCoinName());
        favorite.setUser(user);

        return favoriteRepo.save(favorite);
    }
    public void removeFavorite(Long id) {
        favoriteRepo.deleteById(id);
    }

}
