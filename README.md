# 🚀 High-Performance Rate Limiter & Policy Management System

> **Redis와 Lua 스크립트**를 활용한 고성능 **토큰 버킷(Token Bucket) 알고리즘** 기반의 API 레이트 리미트 및 관리자 정책(CRUD) 시스템입니다.  
> 동시성 제어와 대용량 트래픽 방어 성능을 **k6 부하 테스트**를 통해 정량적으로 검증했습니다.

---

## 💡 Key Features & Architecture

### 1. 동시성 보장을 위한 Redis & Lua Script 기반 토큰 버킷
* **원자성 보장 (Atomic Operation):** 분산 환경 및 다중 스레드 환경에서 발생할 수 있는 레이스 콘디션(Race Condition)을 방지하기 위해, 토큰 조회 및 차감 연산을 **Redis Lua Script**로 단일 원자적 명령 처리.
* **동적 정책 관리:** API 키별로 최대 용량(`capacity`)과 초당 충전량(`refillRate`)을 관리자가 동적으로 생성, 조회, 수정(`CRUD`)할 수 있는 백엔드 API 제공.

### 2. 예외 처리 및 커스텀 응답
* 허용된 트래픽을 초과할 경우, 서버 부하를 방지하고 클라이언트에게 명확한 상태를 전달하기 위해 **HTTP 429 (Too Many Requests)** 응답 및 커스텀 에러 포맷 반환.

---

## 🛠️ Tech Stack

* **Language:** Java 17 / 21
* **Framework:** Spring Boot, Spring Data JPA
* **Database & Cache:** MySQL / H2, **Redis**
* **Containerization:** Docker, Docker Compose
* **Performance Testing:** **k6** (Load & Stress Testing)

---

## 📊 Performance & Load Test Results (k6)

`k6`를 활용해 **초당 약 300회(`~300 RPS`)**의 고강도 요청을 시뮬레이션하여 시스템의 안정성과 레이트 리미트 방어 성능을 검증했습니다.

* **총 요청 수:** 1,504 건 (`~299.17 RPS`)
* **응답 속도:** **`p(95)` 기준 10.24ms**로 매우 쾌적한 처리 속도 유지
* **트래픽 방어율:** 허용 범위를 초과한 **95.74%의 요청(1,440건)을 429 에러로 정확하고 안전하게 차단**
* **시스템 안정성:** 500 에러(서버 오류) 0건, 체크 성공률 **100% 달성**

```text
  ✓ status is 200 or 429

  http_req_duration..............: avg=16.06ms  min=1.76ms  med=3.86ms  max=789.79ms p(90)=7.72ms   p(95)=10.24ms 
  http_req_failed................: 95.74% 1440 out of 1504
  http_reqs......................: 1504   299.171388/s
