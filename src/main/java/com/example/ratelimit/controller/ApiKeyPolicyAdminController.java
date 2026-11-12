package com.example.ratelimit.controller;

import com.example.ratelimit.dto.ApiKeyPolicyRequest;
import com.example.ratelimit.entity.ApiKeyPolicy;
import com.example.ratelimit.service.ApiKeyPolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/policies")
public class ApiKeyPolicyAdminController {

    private final ApiKeyPolicyService policyService;

    public ApiKeyPolicyAdminController(ApiKeyPolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyPolicy>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/{apiKey}")
    public ResponseEntity<ApiKeyPolicy> getPolicy(@PathVariable String apiKey) {
        return ResponseEntity.ok(policyService.getPolicy(apiKey));
    }

    @PostMapping
    public ResponseEntity<ApiKeyPolicy> createPolicy(@RequestBody ApiKeyPolicyRequest request) {
        ApiKeyPolicy created = policyService.createPolicy(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{apiKey}")
    public ResponseEntity<ApiKeyPolicy> updatePolicy(
            @PathVariable String apiKey,
            @RequestBody ApiKeyPolicyRequest request
    ) {
        ApiKeyPolicy updated = policyService.updatePolicy(apiKey, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{apiKey}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String apiKey) {
        policyService.deletePolicy(apiKey);
        return ResponseEntity.noContent().build();
    }
}