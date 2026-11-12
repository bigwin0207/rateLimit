package com.example.ratelimit.config;

import com.example.ratelimit.entity.ApiKeyPolicy;
import com.example.ratelimit.repository.ApiKeyPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final ApiKeyPolicyRepository policyRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            String testKey = "my-test-key-123";

            // 이미 해당 API 키 정책이 존재하지 않을 경우에만 기본값 삽입
            if (policyRepository.findByApiKeyAndActiveTrue(testKey).isEmpty()) {
                ApiKeyPolicy policy = new ApiKeyPolicy();
                policy.setApiKey(testKey);
                policy.setCapacity(60L);      // 최대 토큰 60개
                policy.setRefillRate(1.0);    // 초당 충전량 1개
                policy.setActive(true);

                policyRepository.save(policy);
                System.out.println(">> 기본 API 키 정책이 자동으로 등록되었습니다: " + testKey);
            }
        };
    }
}
