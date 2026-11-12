package com.example.ratelimit.interceptor;
import com.example.ratelimit.dto.RateLimitErrorResponse;
import com.example.ratelimit.dto.RateLimitResult;
import com.example.ratelimit.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;
    private static final long CAPACITY = 60; // 최대 60회
    private static final double REFILL_RATE = 1.0; // 초당 1개 충전

    public RateLimitInterceptor(RateLimitService rateLimitService, ObjectMapper objectMapper) {
        this.rateLimitService = rateLimitService;
        this.objectMapper = objectMapper;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String apiKey = request.getHeader("X-API-KEY");

        if (apiKey == null || apiKey.isBlank()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing API Key");
            return false;
        }

        RateLimitResult result = rateLimitService.isAllowed(apiKey, CAPACITY, REFILL_RATE);

        // 공통 RateLimit 응답 헤더 추가
        response.setHeader("X-RateLimit-Limit", String.valueOf(CAPACITY));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, result.remainingTokens())));

        if (!result.allowed()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");

            // 커스텀 에러 응답 객체 생성
            RateLimitErrorResponse errorResponse = new RateLimitErrorResponse(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Too Many Requests",
                    "요청 제한 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.",
                    LocalDateTime.now()
            );

            // JSON으로 직렬화하여 응답에 작성
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return false;
        }

        return true;
    }
}