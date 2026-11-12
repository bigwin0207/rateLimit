package com.example.ratelimit.dto;

public record RateLimitResult(
        boolean allowed,       // 요청 허용 여부 (true: 통과, false: 429 차단)
        long remainingTokens   // 남은 토큰(요청 가능 횟수)
) {
}