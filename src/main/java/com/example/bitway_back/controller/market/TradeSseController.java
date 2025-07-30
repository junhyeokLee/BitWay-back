package com.example.bitway_back.controller.market;

import com.example.bitway_back.service.market.TradeSseService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
public class TradeSseController {

    private final TradeSseService tradeSseService;

    @GetMapping("/summary")
    public SseEmitter streamSummary(@RequestParam String symbol, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        return tradeSseService.createEmitter(symbol);
    }
}