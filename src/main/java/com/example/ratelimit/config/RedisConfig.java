package com.example.ratelimit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<Long> redisScript() {
        // Redis Lua 스크립트 정의 (토큰 버킷 알고리즘)
        String luaScript = """
            local key = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local refillRate = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            
            local rate_limit = redis.call('hmget', key, 'tokens', 'last_refilled')
            
            local tokens = tonumber(rate_limit[1])
            local last_refilled = tonumber(rate_limit[2])
            
            if not tokens then
                tokens = capacity
                last_refilled = now
            else
                local elapsed = math.max(0, now - last_refilled)
                local filled = elapsed * (refillRate / 1000.0)
                tokens = math.min(capacity, tokens + filled)
                last_refilled = now
            end
            
            if tokens < 1 then
                redis.call('hmset', key, 'tokens', tokens, 'last_refilled', last_refilled)
                return 0
            else
                tokens = tokens - 1
                redis.call('hmset', key, 'tokens', tokens, 'last_refilled', last_refilled)
                return 1
            end
        """;

        return RedisScript.of(luaScript, Long.class);
    }
}