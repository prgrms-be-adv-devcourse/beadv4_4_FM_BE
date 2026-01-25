# Mossy Backend (beadv4_4_FM_BE)

백엔드 단기심화 4기 FM팀의 **Mossy 백엔드 레포지토리**입니다.  
DDD 스타일로 구성된 Spring Boot 애플리케이션이며, **회원 · 마켓 · 결제 · 캐시 · 정산(기부)** 기능을 제공합니다.

---

## 🚀 주요 기능

### **👤 회원 / 판매자**
- 회원 가입 / 로그인 / JWT 인증
- 판매자 요청 → 승인 플로우

### **🛒 마켓**
- 상품 관리  
- 장바구니  
- 주문 → 결제 플로우  

### **💰 캐시(지갑)**
- 유저/판매자 잔액 관리  
- 충전/차감 내역 로그  

### **📦 정산 / 기부**
- 배치 기반 정산 처리  
- 기부 로그 적재  

---

## 🛠️ 기술 스택
- **Java 25**
- **Spring Boot 4**
- Spring Security / JPA / Validation  
- **Redis**, **Elasticsearch**
- **MySQL**
- **AWS S3**
- **QueryDSL**

---

## 📂 프로젝트 구조
```
src/main/java/backend/mossy
├─ boundedContext
│ ├─ auth # 인증/인가, JWT
│ ├─ member # 회원/판매자
│ ├─ market # 상품/주문/결제/카트
│ ├─ cash # 지갑/잔액
│ └─ payout # 정산/기부
├─ global # 공통 설정, 예외, 배치, JPA 베이스
└─ shared # 공유 DTO 및 이벤트
```

---

## ▶️ 실행 방법

### **1) 로컬 실행**
```bash
./gradlew bootRun
```

### **2) 인프라 서비스 (Redis / Elasticsearch) 실행**
```
docker compose up -d
```
---

## 🔐 환경 변수

`.env` 또는 시스템 환경 변수로 설정해야 합니다.  
(`application.yml` 기준)

### **공통**
- `TOSS_SECRET_KEY`
- `CLOUD_AWS_S3_BUCKET`
- `CLOUD_AWS_ACCESS_KEY`
- `CLOUD_AWS_SECRET_KEY`
- `CLOUD_AWS_REGION`

### **운영 (prod)**
- `DB_HOST`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `ENCRYPTION_KEY`

---

## 📚 문서
- **Swagger UI** → http://localhost:8080/mossy-docs  
- **OpenAPI JSON** → http://localhost:8080/v3/api-docs

---

## 🧪 테스트
```
./gradlew test
```
