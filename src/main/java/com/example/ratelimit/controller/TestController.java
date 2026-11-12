package com.example.ratelimit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> testRateLimit() {
        return ResponseEntity.ok("요청이 성공적으로 처리되었습니다! (Rate Limit 통과)");
    }
}