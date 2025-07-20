//package com.example.bitway_back.config;
//
//import com.example.bitway_back.service.exchange.ExchangePriceService;
//import com.example.bitway_back.service.exchange.UpbitPriceService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Map;
//
////
//@Configuration
//public class ExchangeServiceConfig {
//
//    @Bean
//    public Map<String, ExchangePriceService> exchangeServices(
//            UpbitPriceService upbit,
//            BinancePriceService binance,
//            BithumbPriceService bithumb,
//            CoinonePriceService coinone,
//            CoinbasePriceService coinbase,
//            KrakenPriceService kraken
//    ) {
//        return Map.of(
//                "upbit", upbit,
//                "binance", binance,
//                "bithumb", bithumb,
//                "coinone", coinone,
//                "coinbase", coinbase,
//                "kraken", kraken
//        );
//    }
//}
