package com.example.ratelimit.service;

import com.example.ratelimit.dto.ApiKeyPolicyRequest;
import com.example.ratelimit.entity.ApiKeyPolicy;
import com.example.ratelimit.repository.ApiKeyPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ApiKeyPolicyService {

    private final ApiKeyPolicyRepository repository;

    public ApiKeyPolicyService(ApiKeyPolicyRepository repository) {
        this.repository = repository;
    }

    public List<ApiKeyPolicy> getAllPolicies() {
        return repository.findAll();
    }

    public ApiKeyPolicy getPolicy(String apiKey) {
        return repository.findById(apiKey)
                .orElseThrow(() -> new IllegalArgumentException("API Key 정책을 찾을 수 없습니다: " + apiKey));
    }

    @Transactional
    public ApiKeyPolicy createPolicy(ApiKeyPolicyRequest request) {
        if (repository.existsById(request.apiKey())) {
            throw new IllegalArgumentException("이미 존재하는 API Key입니다: " + request.apiKey());
        }
        ApiKeyPolicy policy = new ApiKeyPolicy(request.apiKey(), request.capacity(), request.refillRate());
        return repository.save(policy);
    }

    @Transactional
    public ApiKeyPolicy updatePolicy(String apiKey, ApiKeyPolicyRequest request) {
        ApiKeyPolicy policy = getPolicy(apiKey);
        policy.updatePolicy(request.capacity(), request.refillRate());
        return repository.save(policy);
    }

    @Transactional
    public void deletePolicy(String apiKey) {
        repository.deleteById(apiKey);
    }
}