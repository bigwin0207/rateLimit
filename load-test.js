/*
import http from 'k6/http';
import { check, sleep } from 'k6';

// 테스트 시나리오 설정 (옵션)
export const options = {
  stages: [
    { duration: '5s', target: 10 },   // 5초 동안 가상 사용자(VU)를 10명까지 서서히 증가
    { duration: '10s', target: 10 },  // 10초 동안 10명의 사용자로 유지하며 부하 집중
    { duration: '5s', target: 0 },    // 5초 동안 서서히 종료
  ],
  thresholds: {
    // 전체 요청 중 95% 이상의 요청이 200ms 이내에 응답해야 함
    http_req_duration: ['p(95)<200'],
  },
};

const BASE_URL = 'http://localhost:8081/api/v1/test';

export default function () {
  const headers = {
    'X-API-KEY': 'my-test-key-123', // 앞서 테스트에 사용한 유효한 API 키
    'Content-Type': 'application/json',
  };

  // API 요청 전송
  const res = http.get(BASE_URL, { headers: headers });

  // 응답 검증 (200 OK 또는 레이트 리미트 초과 시 429 Too Many Requests 허용)
  check(res, {
    'status is 200 or 429': (r) => r.status === 200 || r.status === 429,
  });

  // 요청 간 미세한 대기 시간 (필요에 따라 조절)
  sleep(0.05);
}*/

import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  // 동시에 요청을 보낼 가상 유저(VUs) 수
  vus: 20,
  // 테스트 지속 시간
  duration: '5s',
};

export default function () {
  // 도커에서 실행할 경우 host.docker.internal, 로컬 k6면 localhost 사용
  const url = 'http://host.docker.internal:8081/api/v1/test';

  const params = {
    headers: {
      'X-API-KEY': 'my-test-key-123', // DB에 등록된 키 사용
      'Content-Type': 'application/json',
    },
  };

  const res = http.get(url, params);

  // 응답 상태코드가 200(성공) 또는 429(레이트 리미트 초과)인지 검증
  check(res, {
    'status is 200 or 429': (r) => r.status === 200 || r.status === 429,
  });

  sleep(0.05); // 아주 짧은 간격으로 연속 요청
}
