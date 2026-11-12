package com.example.ratelimit.dto;

public record ApiKeyPolicyRequest(
        String apiKey,
        long capacity,
        double refillRate
) {
}
