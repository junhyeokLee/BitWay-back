package com.example.bitway_back.controller;
import com.example.bitway_back.dto.FavoriteDto;
import com.example.bitway_back.domain.FavoriteCoin;
import com.example.bitway_back.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "FavoriteController", description = "즐겨찾기 컨트롤러")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "즐겨찾기 목록 조회 API")
    @GetMapping("/{uuid}")
    public List<FavoriteCoin> list(@PathVariable String uuid) {
        return favoriteService.getFavorites(uuid);
    }

    @Operation(summary = "즐겨찾기 추가 API")
    @PostMapping
    public FavoriteCoin add(@RequestBody FavoriteDto dto) {
        return favoriteService.addFavorite(dto);
    }

    @Operation(summary = "즐겨찾기 삭제 API")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        favoriteService.removeFavorite(id);
    }
}