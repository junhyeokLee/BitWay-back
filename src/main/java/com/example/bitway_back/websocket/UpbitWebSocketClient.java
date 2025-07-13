package com.example.bitway_back.websocket;

import com.example.bitway_back.service.PriceBroadcastService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletionStage;

@Slf4j
@Component
public class UpbitWebSocketClient implements Listener {

    private WebSocket webSocket;
    private static final String MARKET_URL = "https://api.upbit.com/v1/market/all?isDetails=false";
    private static final String WS_URL = "wss://api.upbit.com/websocket/v1";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final PriceBroadcastService broadcastService;

    @Autowired
    public UpbitWebSocketClient(PriceBroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

//    @PostConstruct
    public void connect() {
        List<String> krwMarkets = fetchKrwMarkets();
        if (krwMarkets.isEmpty()) {
            log.warn("No KRW markets found. Skipping WebSocket connection.");
            return;
        }

        webSocket = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(WS_URL), this)
                .join();

        String subscribeMessage = buildSubscribeMessage(krwMarkets);
        webSocket.sendText(subscribeMessage, true);
        log.info("Connected to Upbit WebSocket and subscribed to {} markets", krwMarkets.size());
    }

    private List<String> fetchKrwMarkets() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MARKET_URL))
                    .header("accept", "application/json")
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            List<Map<String, Object>> markets = objectMapper.readValue(
                    response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
            );

            List<String> krwMarkets = new ArrayList<>();
            for (Map<String, Object> market : markets) {
                String marketCode = (String) market.get("market");
                if (marketCode != null && marketCode.startsWith("KRW-")) {
                    krwMarkets.add(marketCode);
                }
            }
            return krwMarkets;

        } catch (IOException | InterruptedException e) {
            log.error("Failed to fetch market list", e);
            return Collections.emptyList();
        }
    }

    private String buildSubscribeMessage(List<String> markets) {
        List<Map<String, Object>> message = new ArrayList<>();
        message.add(Map.of("ticket", "all-krw"));
        message.add(Map.of(
                "type", "ticker",
                "codes", markets
        ));
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to build subscribe message", e);
            return "";
        }
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        Listener.super.onOpen(webSocket);
        log.info("WebSocket opened");
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        log.debug("Received message: {}", data);
        // TODO: 전달받은 데이터를 가공하거나 브로커로 전달
        return Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        String json = new String(bytes);
        log.info("[UpbitWS] Data: {}", json);
        broadcastService.broadcast(json);
        log.info("Broadcasted to clients");
        return Listener.super.onBinary(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        log.error("WebSocket error", error);
        Listener.super.onError(webSocket, error);
    }

    @PreDestroy
    public void disconnect() {
        if (webSocket != null) {
            webSocket.abort();
            log.info("WebSocket closed");
        }
    }
}