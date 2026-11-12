package com.example.ratelimit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "api_key_policy")
public class ApiKeyPolicy {

    @Id
    private String apiKey;
    private long capacity;
    private double refillRate;
    private boolean active = true; // 활성화 여부 필드 추가

    // 기본 생성자 (public으로 변경하여 new ApiKeyPolicy() 허용)
    public ApiKeyPolicy() {}

    public ApiKeyPolicy(String apiKey, long capacity, double refillRate) {
        this.apiKey = apiKey;
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.active = true;
    }

    // Setter 메서드들 추가
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public void setRefillRate(double refillRate) {
        this.refillRate = refillRate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void updatePolicy(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    // Getter 메서드들
    public String getApiKey() {
        return apiKey;
    }

    public long getCapacity() {
        return capacity;
    }

    public double getRefillRate() {
        return refillRate;
    }

    public boolean isActive() {
        return active;
    }
}