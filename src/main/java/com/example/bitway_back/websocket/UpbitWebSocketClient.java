package com.example.bitway_back.websocket;

import com.example.bitway_back.handler.UpbitWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Component
public class UpbitWebSocketClient implements ApplicationRunner {
    private final UpbitWebSocketHandler handler;
    private final ObjectMapper mapper = new ObjectMapper();

    public UpbitWebSocketClient(UpbitWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run(ApplicationArguments args) {
        new Thread(this::connect).start();
    }

    public void connect() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            List<String> codes = fetchMarkets();

            WebSocket webSocket = client.newWebSocketBuilder()
                    .buildAsync(URI.create("wss://api.upbit.com/websocket/v1"), new WebSocket.Listener() {
                        @Override
                        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
                            String json = new String(data.array());
                            handler.broadcast(json);
                            return Listener.super.onBinary(webSocket, data, last);
                        }
                    }).join();

            List<Object> subscribe = List.of(
                    Map.of("ticket", UUID.randomUUID().toString()),
                    Map.of("type", "ticker", "codes", codes)
            );

            String json = mapper.writeValueAsString(subscribe);
            webSocket.sendText(json, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> fetchMarkets() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.upbit.com/v1/market/all?isDetails=false"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Map<String, String>> markets = mapper.readValue(response.body(), List.class);

        return markets.stream()
                .map(m -> m.get("market"))
                .filter(code -> code.startsWith("KRW-"))
                .collect(Collectors.toList());
    }
}