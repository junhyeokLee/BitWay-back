package com.example.bitway_back.service;

import com.example.bitway_back.dto.KimchiPremiumDto;
import com.example.bitway_back.service.exchange.ExchangePriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KimchiPremiumService {

    private final ExchangeRateService exchangeRateService;
    private final Map<String, ExchangePriceService> exchangeServices;

    public KimchiPremiumDto compare(String symbol, String domestic, String overseas) {

        ExchangePriceService domesticService = exchangeServices.get(domestic.toLowerCase(Locale.ROOT));
        ExchangePriceService overseasService = exchangeServices.get(overseas.toLowerCase(Locale.ROOT));

        if (domesticService == null || overseasService == null) {
            throw new IllegalArgumentException("지원하지 않는 거래소: " + domestic + " 또는 " + overseas);
        }

        double domesticPrice = domesticService.getPriceKrw(symbol);
        double overseasPriceUsd = overseasService.getPriceUsd(symbol);
        double exchangeRate = exchangeRateService.getUsdToKrwRate();
        double overseasPriceKrw = overseasPriceUsd * exchangeRate;

        double premium = ((domesticPrice - overseasPriceKrw) / overseasPriceKrw) * 100;

        return KimchiPremiumDto.builder()
                .symbol(symbol)
                .domesticExchange(domestic)
                .domesticPrice(domesticPrice)
                .overseasExchange(overseas)
                .overseasPrice(overseasPriceUsd)
                .exchangeRate(exchangeRate)
                .premiumRate(premium)
                .build();
    }

    public List<KimchiPremiumDto> getAllPremiums(String domestic, String overseas) {
        ExchangePriceService domesticService = exchangeServices.get(domestic.toLowerCase(Locale.ROOT));
        ExchangePriceService overseasService = exchangeServices.get(overseas.toLowerCase(Locale.ROOT));

        if (domesticService == null || overseasService == null) {
            throw new IllegalArgumentException("지원하지 않는 거래소");
        }

        Map<String, Double> domesticPrices = domesticService.getAllPricesKrw(); // e.g., BTC → 1000만 원
        Map<String, Double> overseasPrices = overseasService.getAllPricesUsd(); // e.g., BTC → 8000 USD

        double exchangeRate = exchangeRateService.getUsdToKrwRate();

        List<KimchiPremiumDto> result = new ArrayList<>();

        for (String symbol : domesticPrices.keySet()) {
            if (!overseasPrices.containsKey(symbol)) continue;

            double domesticPrice = domesticPrices.get(symbol);
            double overseasPriceUsd = overseasPrices.get(symbol);
            double overseasPriceKrw = overseasPriceUsd * exchangeRate;

            double premium = ((domesticPrice - overseasPriceKrw) / overseasPriceKrw) * 100;

            result.add(KimchiPremiumDto.builder()
                    .symbol(symbol)
                    .domesticExchange(domestic)
                    .domesticPrice(domesticPrice)
                    .overseasExchange(overseas)
                    .overseasPrice(overseasPriceUsd)
                    .exchangeRate(exchangeRate)
                    .premiumRate(premium)
                    .build());
        }

        return result;
    }

}
