package com.example.bitway_back.api.controller.coin;

import com.example.bitway_back.dto.response.KimchiPremiumDto;
import com.example.bitway_back.api.service.coin.KimchiPremiumRedisCache;
import com.example.bitway_back.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/premium")
@RequiredArgsConstructor
@Tag(name = "KimchiPremiumController", description = "김치 프리미엄 컨트롤러")
public class KimchiPremiumController {
    // KimchiPremiumService 의존성 제거
    private final KimchiPremiumRedisCache cache;

//    @Operation(summary = "김치 프리미엄 비교 API")
//    @GetMapping("/kimp")
//    public ResponseEntity<KimchiPremiumDto> compareKimp(
//            @RequestParam String symbol,
//            @RequestParam String domestic,
//            @RequestParam String overseas
//    ) {
//        KimchiPremiumDto result = kimchiPremiumService.compare(symbol.toUpperCase(),domestic, overseas);
//        return ResponseEntity.ok(result);
//    }

    @Operation(summary = "모든 김치 프리미엄 조회 API")
    @GetMapping
    public List<KimchiPremiumDto> getCachedPremiums(
            @RequestParam(required = false) String domestic,
            @RequestParam(required = false) String overseas,
            @RequestParam(defaultValue = "price_desc") String sortBy
    ) {
        String userId = SecurityUtil.getCurrentUserId();
        if (domestic != null && overseas != null) {
            return cache.getAllFiltered(userId, domestic, overseas, sortBy);
        }
        return cache.getAllSorted(sortBy);
    }
}
