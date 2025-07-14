package com.example.bitway_back.controller;

import com.example.bitway_back.service.PriceBroadcastService;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebSocketTestController {

    private final PriceBroadcastService broadcastService;

    public WebSocketTestController(PriceBroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @GetMapping("/broadcast")
    public String broadcast(@RequestParam("msg") String msg) {
        broadcastService.broadcast(msg);
        return "Broadcasted: " + msg;
    }
}