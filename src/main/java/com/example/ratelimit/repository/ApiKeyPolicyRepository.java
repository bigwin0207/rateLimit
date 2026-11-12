package com.example.ratelimit.repository;

import com.example.ratelimit.entity.ApiKeyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyPolicyRepository extends JpaRepository<ApiKeyPolicy, String> {
    Optional<ApiKeyPolicy> findByApiKeyAndActiveTrue(String apiKey);
}