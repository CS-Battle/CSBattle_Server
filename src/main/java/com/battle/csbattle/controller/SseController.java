package com.battle.csbattle.controller;

import com.battle.csbattle.service.SseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {
    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(value = "/connect/{userId}", produces = "text/event-stream")
    public SseEmitter connect(@PathVariable String userId) {
        return sseService.connect(userId);
    }
}
