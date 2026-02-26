/**
 * 정산(Payout) 서비스 부하 테스트
 *
 * 시나리오:
 *   A. api          - GET /api/v1/payouts 판매자 월별 조회 (50 VU × 60s)
 *   B. batchTiming  - POST /internal/payout/batch/collect 배치 순차 실행 & 처리 시간 측정
 *   C. batchConcurrent - 동시 배치 트리거로 비관적/낙관적 락 경합 확인
 *
 * 사전 조건:
 *   - 서버 실행: spring.profiles.active=loadtest
 *   - 데이터: PayoutLoadTestDataInitializer 가 30,000건 후보 생성
 *
 * 실행:
 *   k6 run load-test/payout-load-test.js
 *   k6 run --env SCENARIO=batchTiming load-test/payout-load-test.js
 *   k6 run --env SCENARIO=batchConcurrent load-test/payout-load-test.js
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

// ── 커스텀 메트릭 ─────────────────────────────────────────────────────────
const apiLatency       = new Trend('payout_api_latency_ms',  true);
const batchLatency     = new Trend('payout_batch_latency_ms', true);
const apiErrorRate     = new Rate('payout_api_error_rate');
const batchErrorRate   = new Rate('payout_batch_error_rate');
const batchCallCount   = new Counter('payout_batch_call_count');

// ── 설정 ─────────────────────────────────────────────────────────────────
const BASE_URL   = __ENV.BASE_URL   || 'http://localhost:8084';
const SCENARIO   = __ENV.SCENARIO   || 'api';

// 판매자 ID 범위 (loadtest 시더 생성 범위: 301~320)
const SELLER_IDS = Array.from({ length: 20 }, (_, i) => 301 + i);

// ── 시나리오 정의 ─────────────────────────────────────────────────────────
const scenarios = {
  // A. API 부하: 50 VU가 60초간 월별 정산 조회
  api: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '10s', target: 50 },   // ramp-up
      { duration: '60s', target: 50 },   // sustained
      { duration: '10s', target: 0  },   // ramp-down
    ],
    exec: 'apiScenario',
  },

  // B. 배치 타이밍: 1 VU가 배치를 순차적으로 5회 실행 → 처리 시간 분포 확인
  batchTiming: {
    executor: 'per-vu-iterations',
    vus: 1,
    iterations: 5,
    exec: 'batchTimingScenario',
  },

  // C. 배치 동시 실행: 10 VU가 동시에 배치 트리거 → 락 경합 확인
  batchConcurrent: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '5s',  target: 10 },
      { duration: '30s', target: 10 },
      { duration: '5s',  target: 0  },
    ],
    exec: 'batchConcurrentScenario',
  },
};

export const options = {
  scenarios: { [SCENARIO]: scenarios[SCENARIO] },

  thresholds: {
    // API: p95 응답시간 < 500ms, 에러율 < 1%
    'payout_api_latency_ms{scenario:api}':    ['p(95)<500'],
    'payout_api_error_rate{scenario:api}':    ['rate<0.01'],

    // 배치: p95 처리시간 < 10s (10,000건 기준)
    'payout_batch_latency_ms':                ['p(95)<10000'],
    'payout_batch_error_rate':                ['rate<0.05'],
  },
};

// ── 시나리오 A: API 부하 테스트 ───────────────────────────────────────────
export function apiScenario() {
  const sellerId = randomOf(SELLER_IDS);
  const year     = 2026;
  const month    = randomInt(1, 12);

  const url = `${BASE_URL}/api/v1/payouts?year=${year}&month=${month}`;
  const params = {
    headers: { 'X-User-Id': String(sellerId) },
    tags:    { scenario: 'api' },
  };

  const res = http.get(url, params);

  const ok = check(res, {
    'status 200':         (r) => r.status === 200,
    'body not empty':     (r) => r.body && r.body.length > 0,
    'has summary field':  (r) => {
      try { return JSON.parse(r.body).data.summary !== undefined; }
      catch { return false; }
    },
  });

  apiLatency.add(res.timings.duration, { scenario: 'api' });
  apiErrorRate.add(!ok);

  sleep(randomFloat(0.1, 0.5));
}

// ── 시나리오 B: 배치 타이밍 측정 ─────────────────────────────────────────
export function batchTimingScenario() {
  const res = triggerCollectBatch();

  const ok = check(res, {
    'batch 200':        (r) => r.status === 200,
    'batch COMPLETED':  (r) => {
      try { return JSON.parse(r.body).status === 'COMPLETED'; }
      catch { return false; }
    },
  });

  batchLatency.add(res.timings.duration);
  batchErrorRate.add(!ok);
  batchCallCount.add(1);

  if (res.status === 200) {
    const body = JSON.parse(res.body);
    console.log(`[배치 타이밍] status=${body.status}, elapsedMs=${body.elapsedMs}ms`);
  }

  sleep(2);
}

// ── 시나리오 C: 동시 배치 락 경합 ────────────────────────────────────────
export function batchConcurrentScenario() {
  const res = triggerCollectBatch();

  const ok = check(res, {
    'batch 200':  (r) => r.status === 200,
    'no 5xx':     (r) => r.status < 500,
  });

  batchLatency.add(res.timings.duration);
  batchErrorRate.add(!ok);
  batchCallCount.add(1);

  if (res.status !== 200) {
    console.warn(`[락 경합] VU=${__VU} status=${res.status} body=${res.body}`);
  }

  sleep(randomFloat(0.5, 2.0));
}

// ── 공통 헬퍼 ─────────────────────────────────────────────────────────────
function triggerCollectBatch() {
  return http.post(
    `${BASE_URL}/internal/payout/batch/collect`,
    null,
    { timeout: '30s' }
  );
}

function randomOf(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomFloat(min, max) {
  return Math.random() * (max - min) + min;
}
