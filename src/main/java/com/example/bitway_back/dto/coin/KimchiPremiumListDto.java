package com.example.bitway_back.dto.coin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KimchiPremiumListDto {
    private List<KimchiPremiumDto> premiums;
}
