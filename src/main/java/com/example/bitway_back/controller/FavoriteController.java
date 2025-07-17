package com.example.bitway_back.controller;
import com.example.bitway_back.dto.FavoriteDto;
import com.example.bitway_back.entity.FavoriteCoin;
import com.example.bitway_back.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/{uuid}")
    public List<FavoriteCoin> list(@PathVariable String uuid) {
        return favoriteService.getFavorites(uuid);
    }

    @PostMapping
    public FavoriteCoin add(@RequestBody FavoriteDto dto) {
        return favoriteService.addFavorite(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        favoriteService.removeFavorite(id);
    }
}
