package com.example.ratelimit.service;

import com.example.ratelimit.dto.RateLimitResult;
import com.example.ratelimit.entity.ApiKeyPolicy;
import com.example.ratelimit.repository.ApiKeyPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final ApiKeyPolicyRepository policyRepository;
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> redisScript;

    /**
     * 인터셉터에서 호출하는 메서드 (DB에서 키별 정책 동적 조회)
     */
    public RateLimitResult isAllowed(String apiKey, long defaultCapacity, double defaultRefillRate) {
        // 1. DB에서 활성화된 API 키 정책 조회 (없으면 기본값 적용)
        ApiKeyPolicy policy = policyRepository.findByApiKeyAndActiveTrue(apiKey)
                .orElseGet(() -> {
                    ApiKeyPolicy defaultPolicy = new ApiKeyPolicy();
                    defaultPolicy.setCapacity(defaultCapacity);
                    defaultPolicy.setRefillRate(defaultRefillRate);
                    return defaultPolicy;
                });

        String redisKey = "rate:limit:" + apiKey;
        long now = System.currentTimeMillis();

        // 2. Redis Lua 스크립트에 동적으로 가져온 capacity와 refillRate 전달
        Long result = redisTemplate.execute(
                redisScript,
                List.of(redisKey),
                String.valueOf(policy.getCapacity()),
                String.valueOf(policy.getRefillRate()),
                String.valueOf(now)
        );

        boolean allowed = (result != null && result == 1L);

        // 남은 토큰 수 계산 (필요시 Redis에서 현재 토큰 값을 조회하거나 정책 기준으로 산출)
        long remainingTokens = allowed ? policy.getCapacity() - 1 : 0;

        return new RateLimitResult(allowed, remainingTokens);
    }
}