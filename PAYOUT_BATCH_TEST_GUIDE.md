# 정산 배치 테스트 가이드

## 📋 목차
1. [사전 준비](#1-사전-준비)
2. [테스트 데이터 확인](#2-테스트-데이터-확인)
3. [배치 실행 방법](#3-배치-실행-방법)
4. [결과 확인](#4-결과-확인)
5. [트러블슈팅](#5-트러블슈팅)

---

## 1. 사전 준비

### 1-1. 환경 설정
```yaml
# application.yml에서 profile을 dev로 설정
spring:
  profiles:
    active: dev
```

### 1-2. 정산 플로우 이해
```
[실시간] 주문 완료
  ↓
PayoutCandidateItem 생성 (정산 후보)
  ↓ (대기 기간: 7일)
  ↓
[배치 Step 1] PayoutCandidateItem → PayoutItem 변환
  ↓
[배치 Step 2] Payout 완료 처리
  ↓
완료! (PayoutCompletedEvent 발행)
```

### 1-3. 필요한 데이터
정산을 테스트하려면 다음 데이터가 필요합니다:
- ✅ **판매자 (PayoutSeller)**: 정산을 받을 사람
- ✅ **구매자 (PayoutUser)**: 돈을 지불한 사람
- ✅ **주문 (OrderItem)**: 결제가 완료된 주문
- ✅ **정산 후보 (PayoutCandidateItem)**: 주문 완료 시 자동 생성

---

## 2. 테스트 데이터 확인

### 2-1. 정산 후보 아이템 확인
```bash
curl http://localhost:8080/api/test/payout-batch/candidates
```

**응답 예시:**
```json
{
  "totalCount": 5,
  "candidates": [
    {
      "id": 1,
      "eventType": "PRODUCT_PRICE",
      "amount": 50000,
      "paymentDate": "2026-01-15T10:30:00",
      "payeeName": "테스트 상점",
      "isProcessed": false,
      "createdAt": "2026-01-15T10:30:00"
    }
  ]
}
```

**확인 사항:**
- `totalCount`가 0이면 → 테스트 데이터가 없습니다
- `isProcessed: false` → 아직 처리되지 않은 정산 후보
- `paymentDate`가 **7일 이상 지났는지** 확인 (PayoutPolicy.PAYOUT_READY_WAITING_DAYS)

---

### 2-2. 현재 통계 확인
```bash
curl http://localhost:8080/api/test/payout-batch/stats
```

**응답 예시:**
```json
{
  "candidates": {
    "total": 10,
    "processed": 3,
    "unprocessed": 7
  },
  "payouts": {
    "total": 2,
    "completed": 1,
    "active": 1
  }
}
```

---

## 3. 배치 실행 방법

### 방법 1️⃣: 전체 배치 Job 실행 (권장)
**Step 1 + Step 2를 순서대로 실행**

```bash
curl -X POST http://localhost:8080/api/test/payout-batch/run
```

**응답 예시:**
```json
{
  "status": "COMPLETED",
  "exitStatus": "COMPLETED",
  "startTime": "2026-01-23T14:30:00",
  "endTime": "2026-01-23T14:30:05",
  "jobId": 123
}
```

**로그 확인:**
```
[정산 항목 수집] 시작 - 청크 크기: 100
[정산 항목 수집] 완료 - 처리된 항목: 7건
[정산 완료 처리] 시작 - 청크 크기: 100
[정산 완료 처리] 완료 - 처리된 정산: 2건
```

---

### 방법 2️⃣: Step별 개별 실행

#### Step 1: 정산 후보 → 정산 아이템 변환
```bash
curl -X POST "http://localhost:8080/api/test/payout-batch/step1?limit=100"
```

**응답 예시:**
```json
{
  "resultCode": "201-1",
  "message": "7건의 정산데이터가 생성되었습니다.",
  "processedCount": 7
}
```

#### Step 2: 정산 완료 처리
```bash
curl -X POST "http://localhost:8080/api/test/payout-batch/step2?limit=100"
```

**응답 예시:**
```json
{
  "resultCode": "201-1",
  "message": "2건의 정산이 처리되었습니다.",
  "processedCount": 2
}
```

---

## 4. 결과 확인

### 4-1. 정산(Payout) 목록 조회
```bash
curl http://localhost:8080/api/test/payout-batch/payouts
```

**응답 예시:**
```json
{
  "totalCount": 2,
  "payouts": [
    {
      "id": 1,
      "payeeName": "테스트 상점",
      "amount": 150000,
      "itemCount": 3,
      "isCompleted": true,
      "payoutDate": "2026-01-23T14:30:05",
      "createdAt": "2026-01-15T10:30:00"
    }
  ]
}
```

**확인 사항:**
- ✅ `isCompleted: true` → 정산 완료됨
- ✅ `payoutDate`가 설정됨 → 정산 완료 시각
- ✅ `itemCount` → 포함된 정산 항목 개수

---

### 4-2. 다시 통계 확인
```bash
curl http://localhost:8080/api/test/payout-batch/stats
```

**배치 실행 전후 비교:**
```
[실행 전]
candidates: { total: 10, unprocessed: 7 }
payouts: { total: 2, completed: 1 }

[실행 후]
candidates: { total: 10, unprocessed: 0 }  ← 모두 처리됨
payouts: { total: 2, completed: 2 }        ← 모두 완료됨
```

---

## 5. 트러블슈팅

### ❌ 문제 1: "처리할 항목이 없습니다"
```
[정산 항목 수집] 처리할 항목이 없습니다.
```

**원인:**
- PayoutCandidateItem이 없음
- 또는 `paymentDate`가 7일이 안 지남

**해결:**
1. 정산 후보 확인: `GET /api/test/payout-batch/candidates`
2. `paymentDate`를 7일 이전으로 수동 변경 (테스트용)

```sql
-- 테스트를 위해 paymentDate를 과거로 변경
UPDATE PAYOUT_CANDIDATE
SET payment_date = DATE_SUB(NOW(), INTERVAL 8 DAY)
WHERE payout_item_id IS NULL;
```

---

### ❌ 문제 2: TransactionManager 관련 에러
```
No bean named 'transactionManager' available
```

**원인:** Spring Batch 6.0에서 TransactionManager가 필수인데 설정되지 않음

**해결:** 이미 코드에 반영되어 있습니다. 빌드 후 재실행하세요.

---

### ❌ 문제 3: "정산이 처리되었습니다" 0건
```
[정산 완료 처리] 처리할 정산이 없습니다.
```

**원인:**
- Step 1이 실행되지 않았거나
- Payout의 amount가 0원

**해결:**
1. Step 1부터 순서대로 실행
2. Payout 확인: `GET /api/test/payout-batch/payouts`

---

### ❌ 문제 4: 배치가 중복 실행되지 않음
```
A job instance already exists and is complete
```

**원인:** Spring Batch는 동일한 JobParameters로 재실행 불가

**해결:** 테스트 컨트롤러는 자동으로 `timestamp`를 추가하므로 문제 없음

---

## 6. 전체 테스트 시나리오

### ✅ 완전한 테스트 플로우

```bash
# 1. 통계 확인 (Before)
curl http://localhost:8080/api/test/payout-batch/stats

# 2. 정산 후보 목록 확인
curl http://localhost:8080/api/test/payout-batch/candidates

# 3. 배치 실행
curl -X POST http://localhost:8080/api/test/payout-batch/run

# 4. 정산 결과 확인
curl http://localhost:8080/api/test/payout-batch/payouts

# 5. 통계 확인 (After)
curl http://localhost:8080/api/test/payout-batch/stats
```

---

## 7. Swagger UI로 테스트하기

더 편하게 테스트하려면 Swagger UI를 사용하세요:

```
http://localhost:8080/mossy-docs
```

**경로:** `payout-batch-test-controller` 섹션에서 모든 API 테스트 가능

---

## 8. 주의사항

⚠️ **이 컨트롤러는 dev 환경에서만 동작합니다**
- `@Profile("dev")` 설정으로 인해 prod 환경에서는 비활성화됨
- 운영 환경에서는 스케줄러만 사용

⚠️ **대기 기간 (7일)**
- `PayoutPolicy.PAYOUT_READY_WAITING_DAYS = 7`
- 테스트 시 DB에서 `paymentDate`를 수동 조정하거나
- 정책 값을 임시로 0으로 변경하여 테스트

⚠️ **청크 크기**
- `application.yml`의 `batch.payout.chunk-size: 100`
- Step별 실행 시 `?limit=10` 파라미터로 조정 가능

---

## 9. 실제 운영 환경

운영 환경에서는 **스케줄러**가 자동으로 실행됩니다:

```java
@Profile("prod")
@Scheduled(cron = "0 0 1 * * *")  // 매일 01:00
@Scheduled(cron = "0 0 4 * * *")  // 매일 04:00
@Scheduled(cron = "0 0 22 * * *") // 매일 22:00
```

테스트 컨트롤러는 개발 환경에서만 사용하세요!
