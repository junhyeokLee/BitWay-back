package com.example.bitway_back.service.coin;

import com.example.bitway_back.domain.coin.FavoriteCoin;
import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.dto.request.FavoriteCoinReqDto;
import com.example.bitway_back.dto.response.FavoriteResDto;
import com.example.bitway_back.exception.CustomException;
import com.example.bitway_back.exception.ErrorCode;
import com.example.bitway_back.repository.FavoriteCoinRepository;
import com.example.bitway_back.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteCoinRepository favoriteRepo;

//    public List<FavoriteCoin> getFavorites() {
//        User user = SecurityUtil.getCurrentUser()
//                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_REQUEST));
//        return favoriteRepo.findByUser(user);
//    }

    public List<FavoriteResDto> getFavorites() {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_REQUEST));

        return favoriteRepo.findByUser(user).stream()
                .map(f -> new FavoriteResDto(
                        f.getId(),
                        f.getSymbol(),
                        f.getAlertEnabled()
                )).toList();
    }

    @Transactional
    public void setFavoriteCoins(FavoriteCoinReqDto request) {
        List<String> symbols = request.getSymbols();

        if (symbols == null || symbols.isEmpty()) {
            throw new IllegalArgumentException("관심 코인을 1개 이상 선택해야 합니다.");
        }

        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_REQUEST));

        for (String symbol : symbols) {
            boolean exists = favoriteRepo.existsByUserAndSymbol(user, symbol);
            if (!exists) {
                FavoriteCoin favorite = FavoriteCoin.builder()
                        .user(user)
                        .symbol(symbol)
                        .alertEnabled(request.getAlertEnabled() != null ? request.getAlertEnabled() : false)
                        .build();
                favoriteRepo.save(favorite);
            }
        }
    }

    public void removeFavorite(Long id) {
        favoriteRepo.deleteById(id);
    }

}
