package com.example.bitway_back.api.controller.coin;
import com.example.bitway_back.dto.request.FavoriteCoinReqDto;
import com.example.bitway_back.dto.response.FavoriteResDto;
import com.example.bitway_back.api.service.coin.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "FavoriteController", description = "즐겨찾기 컨트롤러")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "즐겨찾기 목록 조회 API")
    @GetMapping
    public ResponseEntity<List<FavoriteResDto>> list() {
        return ResponseEntity.ok(favoriteService.getFavorites());
    }

    @Operation(summary = "즐겨찾기 추가 API")
    @PostMapping("/favorite")
    public ResponseEntity<Void> setFavoriteCoins(@RequestBody FavoriteCoinReqDto request) {
        favoriteService.setFavoriteCoins(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "즐겨찾기 삭제 API")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        favoriteService.removeFavorite(id);
    }
}