package com.example.bitway_back.api.service.market;

import com.example.bitway_back.domain.market.LongShortRatio;
import com.example.bitway_back.domain.market.SentimentIndex;
import com.example.bitway_back.api.repository.market.LongShortRatioRepository;
import com.example.bitway_back.api.repository.market.SentimentIndexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MarketSentimentService {

    private final SentimentIndexRepository sentimentIndexRepository;
    private final LongShortRatioRepository longShortRatioRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public void fetchAndSaveSentimentIndex() {
        String url = "https://api.alternative.me/fng/";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, String> data = ((List<Map<String, String>>) response.get("data")).get(0);

        SentimentIndex index = new SentimentIndex();
        index.setValue(Integer.parseInt(data.get("value")));
        index.setClassification(data.get("value_classification"));
        index.setTimestamp(Instant.ofEpochSecond(Long.parseLong(data.get("timestamp")))
                .atZone(ZoneId.systemDefault()).toLocalDateTime());

        sentimentIndexRepository.save(index);
    }

    public void fetchAndSaveLongShortRatio(String symbol) {
        String url = "https://fapi.binance.com/futures/data/globalLongShortAccountRatio?symbol=" + symbol + "&period=1d&limit=1";
        List<Map<String, Object>> result = restTemplate.getForObject(url, List.class);
        if (result == null || result.isEmpty()) return;

        Map<String, Object> data = result.get(0);

        LongShortRatio ratio = new LongShortRatio();
        ratio.setSymbol((String) data.get("symbol"));
        ratio.setLongShortRatio(Double.parseDouble((String) data.get("longShortRatio")));
        ratio.setLongAccount(Double.parseDouble((String) data.get("longAccount")));
        ratio.setShortAccount(Double.parseDouble((String) data.get("shortAccount")));
        ratio.setTimestamp(Instant.ofEpochMilli((Long) data.get("timestamp"))
                .atZone(ZoneId.systemDefault()).toLocalDateTime());

        longShortRatioRepository.save(ratio);
    }
}