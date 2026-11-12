package com.example.ratelimit.dto;

import java.time.LocalDateTime;

public record RateLimitErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {}