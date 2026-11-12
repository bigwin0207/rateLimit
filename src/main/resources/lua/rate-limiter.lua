-- KEYS[1]: 버킷 키 (예: "rate:limit:api_key_123")
-- ARGV[1]: 최대 토큰 수 (Capacity)
-- ARGV[2]: 초당 충전되는 토큰 수 (Refill Rate)
-- ARGV[3]: 현재 시간 (Epoch Milliseconds)
-- ARGV[4]: 요청한 토큰 수 (일부러 1개씩 차감)

local key = KEYS[1]
local max_tokens = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

-- 기존 버킷 상태 조회 (tokens, last_refilled)
local bucket = redis.call('HMGET', key, 'tokens', 'last_refilled')
local tokens = tonumber(bucket[1])
local last_refilled = tonumber(bucket[2])

if not tokens then
    -- 최초 요청인 경우 버킷을 가득 채운 상태로 초기화
    tokens = max_tokens
    last_refilled = now
else
    -- 경과 시간에 따른 토큰 충전 계산
    local elapsed = (now - last_refilled) / 1000.0
    if elapsed > 0 then
        local generated = elapsed * refill_rate
        tokens = math.min(max_tokens, tokens + generated)
        last_refilled = now
    end
end

-- 토큰이 충분한지 확인 후 차감
if tokens >= requested then
    tokens = tokens - requested
    redis.call('HSET', key, 'tokens', tokens, 'last_refilled', last_refilled)
    -- 버킷 키의 만료 시간 설정 (메모리 관리 목적, 예: 최대 충전 시간 기준)
    redis.call('EXPIRE', key, math.ceil(max_tokens / refill_rate) + 60)
    return {1, math.floor(tokens)} -- 허용 및 남은 토큰 반환
else
    redis.call('HSET', key, 'tokens', tokens, 'last_refilled', last_refilled)
    return {0, math.floor(tokens)} -- 차단 및 남은 토큰 반환
end