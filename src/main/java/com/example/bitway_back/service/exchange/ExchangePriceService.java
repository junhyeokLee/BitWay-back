package com.example.bitway_back.service.exchange;

public interface ExchangePriceService {
    double getPriceKrw(String symbol); // 원화 기준 가격
    double getPriceUsd(String symbol); // USD 기준 가격
}
