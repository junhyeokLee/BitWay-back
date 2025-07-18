package com.example.bitway_back.service;

import com.example.bitway_back.dto.FavoriteDto;
import com.example.bitway_back.domain.FavoriteCoin;
import com.example.bitway_back.domain.User;
import com.example.bitway_back.repository.FavoriteCoinRepository;
import com.example.bitway_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteCoinRepository favoriteRepo;
    private final UserRepository userRepo;

    public List<FavoriteCoin> getFavorites(String uuid) {
        return favoriteRepo.findByUser_Uuid(uuid);
    }

    public FavoriteCoin addFavorite(FavoriteDto dto) {
        User user = userRepo.findByUuid(dto.getUuid())
                .orElseGet(() -> userRepo.save(User.builder().uuid(dto.getUuid()).build()));

        FavoriteCoin favorite = FavoriteCoin.builder()
                .coinCode(dto.getCoinCode())
                .coinName(dto.getCoinName())
                .alertEnabled(false)
                .alertPrice(null)
                .user(user)
                .build();

        return favoriteRepo.save(favorite);
    }


    public void removeFavorite(Long id) {
        favoriteRepo.deleteById(id);
    }
}
