# MOSSY — MSA 기반 AI 커머스 플랫폼 기술 문서

> 본 문서는 Google NotebookLM RAG 지식 베이스 주입용으로 작성되었습니다.  
> 작성일: 2026-02-23

---

## 목차

1. [프로젝트 아키텍처 개요](#1-프로젝트-아키텍처-개요)
2. [Common 모듈](#2-common-모듈)
3. [Kafka 모듈](#3-kafka-모듈)
4. [Gateway 모듈](#4-gateway-모듈)
5. [Auth 모듈](#5-auth-모듈)
6. [Member 모듈](#6-member-모듈)
7. [Product 모듈](#7-product-모듈)
8. [Market 모듈](#8-market-모듈)
9. [Cash 모듈](#9-cash-모듈)
10. [Payout 모듈](#10-payout-모듈)
11. [Review 모듈](#11-review-모듈)
12. [AI 모듈](#12-ai-모듈)

---

# 1. 프로젝트 아키텍처 개요

## 1.1 프로젝트 소개

**MOSSY(모시)**는 Spring Boot 3.x 기반의 멀티모듈 MSA(Microservices Architecture) 이커머스 플랫폼입니다. 총 **10개의 마이크로서비스**와 **2개의 공유 라이브러리 모듈**로 구성되며, Apache Kafka를 통한 이벤트 드리븐 아키텍처와 OpenAI 임베딩 벡터 기반 AI 추천 시스템을 핵심 차별점으로 갖습니다.

## 1.2 기술 스택 요약

| 구분 | 기술 |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.4.1, Spring WebFlux (AI 서비스) |
| **Build** | Gradle Kotlin DSL, 멀티모듈 |
| **Architecture** | MSA, Hexagonal (Port & Adapter), Event-Driven |
| **Gateway** | Spring Cloud Gateway + JWT 필터 |
| **메시징** | Apache Kafka (이벤트 기반 비동기 통신) |
| **DB** | PostgreSQL (JPA/Hibernate), R2DBC (AI 서비스) |
| **벡터 검색** | pgvector (코사인 유사도) |
| **AI** | OpenAI API (text-embedding-3-small, gpt-4o-mini) |
| **캐시/세션** | Redis (Refresh Token 저장, 캐시) |
| **검색** | Elasticsearch (상품 카탈로그 검색) |
| **인증** | JWT (Access/Refresh Token), OAuth2 (Google, Kakao) |
| **API 문서** | SpringDoc OpenAPI (Swagger UI) |
| **컨테이너** | Docker Compose |

## 1.3 서비스 포트 맵

| 서비스 | 포트 | 역할 |
|---|---|---|
| Gateway | 8080 | API 라우팅, JWT 인증 필터 |
| Market | 8081 | 주문, 장바구니, 쿠폰 |
| Member | 8082 | 회원, 판매자 관리 |
| Cash | 8083 | 예치금 지갑, 결제 |
| Payout | 8084 | 정산, 기부금 |
| Review | 8085 | 리뷰 |
| Auth | 8086 | 인증, 토큰 발급 |
| AI | 8087 | AI 추천 |
| Product | 8090 | 상품, 카탈로그, 카테고리 |

## 1.4 모듈 의존성 구조

```
common (공유 라이브러리) ← 모든 서비스 모듈이 의존
kafka  (카프카 라이브러리) ← member, market, cash, payout, review가 의존

gateway → (JWT 검증만, 다른 모듈 의존 없음)
auth → member (Feign Client)
member → auth (Feign Client), kafka
product → kafka (예정)
market → product (Feign Client), kafka
cash → kafka
payout → kafka
review → kafka
ai → product (Feign Client)
```

## 1.5 Hexagonal Architecture 패키지 컨벤션

모든 서비스 모듈은 동일한 패키지 구조를 따릅니다:

```
com.mossy.boundedContext/
├── in/          # Inbound Adapter (Controller, EventListener, DTO)
├── app/         # Application Layer (Facade, UseCase)
│   ├── mapper/  # MapStruct 매퍼
│   └── usecase/ # 개별 비즈니스 UseCase
├── domain/      # Domain Entity, Value Object, Policy
└── out/         # Outbound Adapter (Repository, FeignClient, Kafka Publisher)
    ├── repository/
    ├── external/
    └── kafka/
```

- **Facade**: 여러 UseCase를 조합하는 오케스트레이터
- **UseCase**: 단일 책임 원칙에 따른 개별 비즈니스 로직 단위
- **EventPublisher**: `ApplicationEventPublisher`를 래핑하여 Spring 내부 이벤트 발행
- **@TransactionalEventListener(AFTER_COMMIT)**: 트랜잭션 커밋 후 Kafka 이벤트 발행

---

# 2. Common 모듈

## 2.1 모듈 개요

**common**은 모든 마이크로서비스가 공유하는 **라이브러리 모듈**입니다. 자체적으로 실행되지 않으며, 도메인 간 공유가 필요한 **Base Entity, 이벤트 클래스, 열거형, DTO(Payload), 공통 설정**을 제공합니다. MSA 환경에서 서비스 간 계약(Contract)을 정의하는 핵심 모듈입니다.

## 2.2 기술 스택

- `spring-boot-starter-data-jpa` (JPA 공통 엔티티 상속)
- `spring-boot-starter-data-redis` (Redis 설정 공유)
- `spring-boot-starter-validation`
- `springdoc-openapi-starter-common` (Swagger 공통 설정)

## 2.3 핵심 도메인 모델

### 2.3.1 Base Entity 계층

```
BaseEntity (abstract) — 모든 엔티티의 최상위
├── BaseIdAndTime — auto-increment PK + createdAt/updatedAt
└── BaseManualIdAndTime — 수동 PK + createdAt/updatedAt
```

- **BaseEntity**: `getId()`, `getCreatedAt()`, `getUpdatedAt()` 추상 메서드 정의, 내부에서 `publishEvent()`로 스프링 이벤트 발행 가능
- **BaseIdAndTime**: `@GeneratedValue(IDENTITY)` 전략의 자동 증가 PK
- **BaseManualIdAndTime**: 외부에서 ID를 직접 할당하는 엔티티용 (AI 모듈의 `RecommendItem` 등)

### 2.3.2 공유 도메인 엔티티

| 클래스 | 설명 |
|---|---|
| **BaseUser** | `@MappedSuperclass`. 이메일, 이름, 닉네임, 주소, 위경도, 상태(UserStatus) 필드. member/market/cash/payout에서 상속 |
| **BaseSeller** | `@MappedSuperclass`. userId, 판매자유형(SellerType), 상호명, 사업자번호, 위경도, 상태(SellerStatus) 필드 |
| **Role** | 역할 엔티티(USER, SELLER, ADMIN) |

### 2.3.3 공유 이벤트 클래스 (Kafka 메시지 계약)

| 이벤트 | 토픽 | 발행자 | 소비자 |
|---|---|---|---|
| `UserJoinedEvent(UserPayload)` | user.joined | member | market, cash, payout |
| `SellerJoinedEvent(SellerPayload)` | seller.joined | member | cash, payout |
| `ProductCreatedEvent` | (내부 이벤트) | product | ai |
| `ProductUpdatedEvent` | (내부 이벤트) | product | ai |
| `OrderCancelEvent` | order.cancel | market | cash |
| `OrderPurchaseConfirmedEvent` | order.purchase.confirmed | market | payout |
| `OrderStockReturnEvent` | order.stock.return | market | product |
| `PaymentCashRefundEvent` | payment.refund | market | cash |
| `PayoutSellerWalletCreditEvent` | payout.wallet.credit | payout | cash |

### 2.3.4 공유 열거형(Enum)

| 패키지 | Enum | 값 |
|---|---|---|
| member | `UserStatus` | PENDING, ACTIVE, SUSPENDED, DELETED |
| member | `SellerStatus` | ACTIVE, SUSPENDED |
| member | `SellerType` | INDIVIDUAL, BUSINESS |
| member | `SellerRequestStatus` | NONE, PENDING, APPROVED, REJECTED, CANCELED |
| member | `RoleCode` | USER, SELLER, ADMIN |
| product | `ProductStatus` | UNDER_REVIEW, FOR_SALE, PRE_ORDER, OUT_OF_STOCK, DISCONTINUED, SUSPENDED, REJECTED, HIDDEN, DELETED |
| product | `ProductItemStatus` | UNDER_REVIEW, PRE_ORDER, ON_SALE, OUT_OF_STOCK, STOPPED, SUSPENDED, REJECTED, HIDDEN, DELETED |
| market | `OrderState` | PENDING, EXPIRED, PAID, FAILED, CANCELED, CONFIRMED |
| market | `CouponType` | FIXED, PERCENTAGE |
| cash | `PayMethod` | CARD, CASH |
| cash | `PaymentStatus` | READY, PAID, FAILED, CANCELED, PARTIAL_CANCELED, REFUND_IN_PROGRESS |
| payout | `CarbonGrade` | GRADE_1~GRADE_10 (탄소배출량 기반 기부비율) |
| payout | `PayoutEventType` | 정산__상품판매_대금, 정산__상품판매_수수료, 정산__상품판매_기부금 등 |

### 2.3.5 공통 응답 구조 (RsData)

```java
@JsonPropertyOrder({"resultCode", "msg", "data"})
public class RsData<T> {
    private String resultCode;  // "S-200", "F-400" 등
    private String msg;
    private T data;
}
```

모든 API 응답은 `RsData`로 래핑됩니다. `RsData.success(SuccessCode, data)` / `RsData.fail(ErrorCode)` 팩토리 메서드로 생성합니다.

### 2.3.6 EventPublisher

```java
@Service
public class EventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public void publish(Object event) {
        applicationEventPublisher.publishEvent(event);
    }
}
```

Spring의 `ApplicationEventPublisher`를 래핑한 서비스로, Facade/UseCase에서 도메인 이벤트를 발행할 때 사용합니다. 발행된 이벤트는 같은 JVM 내의 `@TransactionalEventListener`가 수신하고, Kafka로 전달합니다.

---

# 3. Kafka 모듈

## 3.1 모듈 개요

**kafka**는 Apache Kafka 관련 공통 인프라를 제공하는 **라이브러리 모듈**입니다. 토픽 상수 정의, Producer/Consumer 설정, 이벤트 발행 유틸리티, Outbox 패턴 구현을 포함합니다.

## 3.2 기술 스택

- `spring-kafka` (Producer/Consumer)
- `spring-boot-starter-data-jpa` (Outbox 테이블 관리)
- JSON 직렬화/역직렬화 (`JsonSerializer`, `JsonDeserializer`)

## 3.3 핵심 구성요소

### 3.3.1 KafkaTopics (토픽 상수)

```java
public class KafkaTopics {
    public static final String PAYMENT_REFUND = "payment.refund";
    public static final String ORDER_CANCEL = "order.cancel";
    public static final String ORDER_STOCK_RETURN = "order.stock.return";
    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String USER_JOINED = "user.joined";
    public static final String USER_UPDATED = "user.updated";
    public static final String SELLER_JOINED = "seller.joined";
    public static final String SELLER_UPDATED = "seller.updated";
    public static final String ORDER_PURCHASE_CONFIRMED = "order.purchase.confirmed";
    public static final String ORDER_REFUNDED = "order.refunded";
    public static final String PAYOUT_WALLET_CREDIT = "payout.wallet.credit";
}
```

### 3.3.2 KafkaEventPublisher (이벤트 발행 유틸)

```java
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(Object event) {
        String topic = resolveTopicName(event);
        kafkaTemplate.send(topic, event);
    }

    private String resolveTopicName(Object event) {
        return switch (event) {
            case UserJoinedEvent e -> KafkaTopics.USER_JOINED;
            case SellerJoinedEvent e -> KafkaTopics.SELLER_JOINED;
            case OrderCancelEvent e -> KafkaTopics.ORDER_CANCEL;
            case PaymentCashRefundEvent e -> KafkaTopics.PAYMENT_REFUND;
            // ... 생략
            default -> null;
        };
    }
}
```

**작동 원리**: 이벤트 객체의 타입을 기반으로 Java 21 `switch` 패턴 매칭으로 토픽을 자동 결정합니다. 각 서비스의 `@TransactionalEventListener`가 Spring 이벤트를 수신하면 이 Publisher를 호출하여 Kafka로 전달합니다.

### 3.3.3 Outbox 패턴

market 모듈에서 사용하는 **Transactional Outbox Pattern** 구현입니다.

- **OutboxEvent**: `PENDING → PROCESSING → PUBLISHED/FAILED` 상태를 가지는 엔티티
- **OutboxPublisher**: 트랜잭션 내에서 이벤트를 `outbox_event` 테이블에 저장 (`Propagation.MANDATORY`)
- **OutboxEventRepository**: 상태별 폴링 조회
- **폴링 스케줄러**: market 모듈의 `OutboxPollerScheduler`가 주기적으로 `PENDING` 이벤트를 조회하여 Kafka로 발행

이 패턴은 DB 트랜잭션과 Kafka 발행의 **원자성**을 보장합니다.

### 3.3.4 KafkaConfig (공통 설정)

```java
@EnableKafka
@Configuration
public class KafkaConfig {
    // bootstrap-servers, group-id는 각 서비스의 application.yml에서 주입
    // Producer: StringSerializer + JsonSerializer
    // Consumer: StringDeserializer + JsonDeserializer (trusted packages: com.mossy.*)
}
```

---

# 4. Gateway 모듈

## 4.1 모듈 개요

**Gateway**는 모든 클라이언트 요청의 **단일 진입점(Single Entry Point)**입니다. Spring Cloud Gateway 기반으로 JWT 인증 필터링, 하위 서비스 라우팅, `X-User-Id`/`X-Seller-Id`/`X-User-Role` 헤더 주입을 담당합니다.

## 4.2 기술 스택

- **Spring Cloud Gateway** (WebFlux 기반 리액티브 게이트웨이)
- **Spring Security WebFlux** (CSRF 비활성화, Stateless)
- **JJWT** (JWT 토큰 파싱/검증)
- DB 미사용 (`DataSourceAutoConfiguration` 제외)

## 4.3 핵심 비즈니스 로직

### 4.3.1 JWT 인증 필터 (`JwtAuthenticationFilter`)

```java
@Component("JwtAuthenticationFilter")
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<Config> {
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Authorization 헤더 존재 확인
            // 2. Bearer 형식 검증
            // 3. JWT parseClaims → userId 추출
            // 4. X-User-Id, X-Seller-Id, X-User-Role 헤더 주입
            // 5. 하위 서비스로 전달
        };
    }
}
```

**작동 원리**:
1. 클라이언트가 `Authorization: Bearer {JWT}` 헤더로 요청
2. Gateway가 JWT를 파싱하여 `userId`, `sellerId`, `role`을 추출
3. 내부 헤더(`X-User-Id`, `X-Seller-Id`, `X-User-Role`)에 주입
4. 하위 서비스는 이 헤더로 유저를 식별 (JWT 재검증 불필요)
5. 만료/변조 시 401 JSON 에러 응답 반환

### 4.3.2 라우팅 규칙

| 경로 패턴 | 대상 서비스 | 인증 필터 |
|---|---|---|
| `/api/v1/cart/**`, `/api/v1/orders/**`, `/api/v1/payments/**` | market (8081) | ✅ JWT 필수 |
| `/api/v1/users/signup` | member (8082) | ❌ 공개 |
| `/api/v1/users/me`, `/password`, `/address`, `/phone` 등 | member (8082) | ✅ JWT 필수 |
| `/api/v1/admin/seller-requests/**` | member (8082) | ✅ JWT 필수 |
| `/api/v1/cash/**` | cash (8083) | ✅ JWT 필수 |
| `/api/payout/**` | payout (8084) | ✅ JWT 필수 |
| `/api/v1/review/**` | review (8085) | ✅ JWT 필수 |
| `/api/v1/auth/**` | auth (8086) | ❌ 공개 |
| `/oauth2/**`, `/login/oauth2/**` | auth (8086) | ❌ 공개 |
| `/api/v1/product/**` | product (8090) | 설정에 따라 |

### 4.3.3 보안 설정

- `/internal/**` 경로는 **게이트웨이에서 완전 차단** (`denyAll`) — 내부 서비스 간 직접 호출 전용
- CSRF, FormLogin, HttpBasic 모두 비활성화
- Stateless 세션 (NoOpServerSecurityContextRepository)

---

# 5. Auth 모듈

## 5.1 모듈 개요

**Auth**는 인증(Authentication) 전담 서비스입니다. 이메일/비밀번호 로그인, OAuth2 소셜 로그인(Google, Kakao), JWT 토큰 발급/재발급/로그아웃을 처리합니다. 사용자 데이터는 직접 보유하지 않고 **Member 서비스를 Feign Client로 호출**하여 검증합니다.

## 5.2 기술 스택

- **Spring Security** (OAuth2 Client 포함)
- **Redis** (Refresh Token 해시 저장, Lua 스크립트 기반 원자적 토큰 교체)
- **OpenFeign** (Member 서비스 호출)
- **JJWT** (JWT 생성/파싱)
- **MapStruct** (DTO 변환)

## 5.3 핵심 도메인 모델

Auth 모듈은 **자체 DB 테이블이 없습니다**. Redis에 Refresh Token만 저장합니다.

| 저장소 | 키 패턴 | 값 | TTL |
|---|---|---|---|
| Redis | `RT:USER:{userId}` | HMAC-SHA256 해시된 Refresh Token | refreshTokenExpireMs |

## 5.4 핵심 비즈니스 로직

### 5.4.1 일반 로그인 흐름 (`AuthFacade.login`)

```
1. AuthFacade.login(email, password)
2. → LoginUseCase: MemberFeignClient.verify(email, password) 호출
3. ← Member가 비밀번호 검증 후 userId, roles 반환
4. → IssueTokenUseCase: Access Token + Refresh Token 생성
5. → RefreshTokenUseCase.save: RT를 HMAC 해시하여 Redis 저장
6. ← LoginResponse(accessToken, refreshToken) 반환
```

### 5.4.2 OAuth2 소셜 로그인 흐름 (`AuthFacade.upsertUserAndIssueToken`)

```
1. Google/Kakao OAuth2 인증 완료
2. OAuth2AuthenticationSuccessHandler가 OAuth2UserInfoImpl에서 provider, email, name 추출
3. AuthFacade.upsertUserAndIssueToken(OAuth2UserDTO) 호출
4. → MemberFeignClient.processSocialLogin() — 유저 생성 or 기존 유저 반환
5. → IssueTokenUseCase: 토큰 발급
6. 실패 시 MemberFeignClient.rollbackSocialLogin() — 보상 트랜잭션
```

### 5.4.3 토큰 재발급 (`ReissueTokenUseCase`)

```
1. 클라이언트가 쿠키의 Refresh Token으로 /api/v1/auth/reissue 호출
2. JwtProvider로 서명/만료 검증 후 userId 추출
3. MemberFeignClient.getAuthInfo(userId) — 최신 권한/상태 조회
4. RefreshTokenUseCase.rotate() — Redis Lua 스크립트로 원자적 토큰 교체
   - 성공(1): 새 RT 저장
   - 세션없음(0): 만료/로그아웃 처리
   - 불일치(-1): 탈취 의심, 에러
5. 새 Access Token + Refresh Token 반환
```

### 5.4.4 Refresh Token 보안 (Redis Lua Script)

```lua
local cur = redis.call('GET', key)
if not cur then return 0 end          -- 세션 없음
if cur ~= old then return -1 end      -- 토큰 불일치 (탈취 의심)
redis.call('SET', key, newv, 'PX', ttl)
return 1                              -- 교체 성공
```

원자적 비교-교체(Compare-And-Swap) 패턴으로 **Refresh Token 재사용 공격**을 방지합니다.

## 5.5 MSA 통신

### Feign Client 호출 (Auth → Member)

| 메서드 | 엔드포인트 | 용도 |
|---|---|---|
| `verify()` | `POST /internal/v1/users/verify` | 이메일/비밀번호 검증 |
| `getAuthInfo()` | `GET /internal/v1/users/{userId}` | 유저 권한/상태 조회 (토큰 재발급용) |
| `processSocialLogin()` | `POST /internal/v1/users/social-login` | 소셜 유저 생성/조회 |
| `rollbackSocialLogin()` | `DELETE /internal/v1/users/social-login/{userId}/rollback` | 보상 트랜잭션 |

### REST API 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| POST | `/api/v1/auth/login` | 이메일/비밀번호 로그인 |
| POST | `/api/v1/auth/reissue` | 토큰 재발급 (쿠키 RT) |
| POST | `/api/v1/auth/logout` | 로그아웃 |
| POST | `/api/v1/auth/seller-approved` | 판매자 승인 후 SELLER 토큰 발급 |
| GET | `/api/v1/auth/ping` | 헬스체크 |

---

# 6. Member 모듈

## 6.1 모듈 개요

**Member**는 회원 관리 서비스입니다. 회원가입, 프로필 관리, 소셜 로그인 연동, 판매자 신청/승인, 관리자(Admin) 기능을 담당합니다. 회원가입 시 `UserJoinedEvent`를 Kafka로 발행하여 다른 서비스(market, cash, payout)에 유저 동기화를 트리거합니다.

## 6.2 기술 스택

- **Spring Security** (비밀번호 암호화)
- **Spring Data JPA** + PostgreSQL
- **OpenFeign** (Auth 서비스 호출)
- **Kafka** (회원 이벤트 발행)
- **AES 암호화** (주민번호, 전화번호 등 개인정보 암호화)
- **MapStruct** (DTO 변환)

## 6.3 핵심 도메인 모델

### 6.3.1 User 엔티티 계층

```
BaseUser (@MappedSuperclass, common)
└── SourceUser (@MappedSuperclass)
    └── User (@Entity, table=USERS)
        ├── rrnEncrypted (주민번호, AES 암호화)
        ├── phoneNum (전화번호, AES 암호화)
        ├── password (BCrypt 해시)
        ├── userRoles: List<UserRole> (N:M)
        └── socialAccounts: List<UserSocialAccount> (1:N)
```

- **ReplicaUser**: 다른 서비스에서 사용하는 읽기 전용 유저 복제본

### 6.3.2 Seller 엔티티 계층

```
BaseSeller (@MappedSuperclass, common)
└── SourceSeller (@MappedSuperclass)
    └── Seller (@Entity)
```

- **SellerRequest**: 판매자 신청 엔티티 (PENDING → APPROVED/REJECTED)

### 6.3.3 UserSocialAccount 엔티티

소셜 로그인 연동 정보 (provider, providerId)를 저장합니다.

## 6.4 핵심 비즈니스 로직

### 6.4.1 회원가입 (`SignupUseCase`)

```
1. 이메일 중복 확인 (소셜 계정 연동 이메일이면 소셜 로그인 유도)
2. 닉네임 중복 확인
3. 비밀번호 BCrypt 암호화
4. 전화번호/주소/주민번호 AES 암호화
5. User 엔티티 생성 + UserRole(USER) 할당
6. DB 저장
7. UserFacade에서 EventPublisher.publish(UserJoinedEvent) 발행
8. @TransactionalEventListener → KafkaEventPublisher.publish() → Kafka "user.joined" 토픽으로 발행
```

### 6.4.2 판매자 신청/승인 흐름

```
1. 유저가 판매자 신청 (SellerRequest 생성, status=PENDING)
2. Admin이 GET /api/v1/admin/seller-requests 로 대기 목록 조회
3. Admin이 POST /api/v1/admin/seller-requests/{id}/approve 로 승인
4. Seller 엔티티 생성 + UserRole(SELLER) 추가
5. SellerJoinedEvent Kafka 발행 → cash, payout에서 판매자 지갑 생성
6. AuthFeignClient.issueForSellerApproved() → SELLER 권한 토큰 발급
```

### 6.4.3 개인정보 암호화/복호화 (`EncryptionUtils`)

- 저장 시: AES 암호화하여 DB에 저장 (전화번호, 주소, 주민번호)
- 조회 시: `UserInfoDecryptor`가 복호화하여 응답

## 6.5 MSA 통신

### Kafka 이벤트 발행

| 이벤트 | 토픽 | 트리거 |
|---|---|---|
| `UserJoinedEvent` | user.joined | 회원가입 완료 |
| `SellerJoinedEvent` | seller.joined | 판매자 승인 완료 |

### Feign Client 호출 (Member → Auth)

| 메서드 | 엔드포인트 | 용도 |
|---|---|---|
| `issueForSellerApproved()` | `POST /api/v1/auth/seller-approved` | 판매자 승인 후 토큰 발급 |

### 내부 API (Auth → Member, Internal)

| Method | Path | 설명 |
|---|---|---|
| POST | `/internal/v1/users/verify` | 이메일/비밀번호 검증 |
| GET | `/internal/v1/users/{userId}` | 인증 정보 조회 |
| POST | `/internal/v1/users/social-login` | 소셜 로그인 처리 |
| DELETE | `/internal/v1/users/social-login/{userId}/rollback` | 보상 트랜잭션 |

### REST API 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| POST | `/api/v1/users/signup` | 회원가입 |
| GET | `/api/v1/users/me` | 마이페이지 |
| PATCH | `/api/v1/users/profile` | 프로필 수정 |
| PATCH | `/api/v1/users/password` | 비밀번호 변경 |
| PATCH | `/api/v1/users/address` | 주소 변경 |
| PATCH | `/api/v1/users/phone` | 전화번호 변경 |
| PATCH | `/api/v1/users/nickname` | 닉네임 변경 |
| POST | `/api/v1/users/set-password` | 소셜 계정 최초 비밀번호 설정 |
| POST | `/api/v1/users/seller-request` | 판매자 신청 |
| GET | `/api/v1/admin/seller-requests` | 판매자 신청 목록 (Admin) |
| POST | `/api/v1/admin/seller-requests/{id}/approve` | 판매자 승인 (Admin) |

---

# 7. Product 모듈

## 7.1 모듈 개요

**Product**는 상품 및 카탈로그 관리 서비스입니다. 카탈로그(표준 상품), 상품(판매자별 상품), 카테고리, 옵션 그룹, 재고 관리를 담당합니다. Elasticsearch를 통한 상품 검색, 상품 등록 시 AI 모듈에 임베딩 동기화 이벤트 발행을 지원합니다.

## 7.2 기술 스택

- **Spring Data JPA** + PostgreSQL
- **Elasticsearch** (상품 카탈로그 검색)
- **QueryDSL** (복잡한 상품 조회 쿼리)
- **OpenFeign** (외부 서비스 호출)
- **AWS S3** (상품 이미지 저장)
- **MapStruct** (DTO 변환)

## 7.3 핵심 도메인 모델

### 7.3.1 Catalog-Product 구조

```
CatalogProduct (표준 상품 카탈로그)
├── Category (카테고리)
├── CatalogImage (카탈로그 이미지)
└── Product (판매자별 상품, N개)
    ├── ProductItem (옵션 조합별 SKU)
    ├── ProductOptionGroup (옵션 그룹)
    └── ProductOptionValue (옵션 값)
```

- **CatalogProduct**: 표준화된 상품 정보 (예: "나이키 에어맥스 90")
- **Product**: 특정 판매자의 판매 상품 (가격, 재고 포함)
- **ProductItem**: 옵션 조합별 단위 (예: "블랙 / 270mm", 재고 수량 포함)

### 7.3.2 Elasticsearch 문서

```java
@Document(indexName = "catalog_products")
public class CatalogDocument {
    // Elasticsearch에 인덱싱되는 카탈로그 검색 문서
}
```

## 7.4 핵심 비즈니스 로직

### 7.4.1 상품 등록 (`RegisterProductUseCase`)

```
1. 판매자가 CatalogProduct 선택 후 상품 등록 요청
2. Product 엔티티 생성 (판매자ID, 가격, 설명)
3. ProductItem 생성 (옵션 조합별 SKU, 재고)
4. ProductOptionGroup/Value 매핑
5. EventPublisher로 ProductCreatedEvent 발행
6. → AI 모듈에서 수신하여 OpenAI 임베딩 벡터 생성
7. Elasticsearch 카탈로그 동기화 이벤트 발행
```

### 7.4.2 카탈로그 검색 (`CatalogSearchUseCase`)

Elasticsearch를 통한 전문 검색으로 카테고리, 가격 범위, 정렬 조건 등으로 카탈로그 상품을 검색합니다.

### 7.4.3 재고 관리

- `DecreaseStockUseCase`: 주문 시 재고 차감
- `IncreaseStockUseCase`: 주문 취소 시 재고 복원 (Kafka `order.stock.return` 이벤트 수신)

## 7.5 MSA 통신

### REST API 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/product/{catalogProductId}` | 상품 상세 조회 |
| POST | `/api/v1/product` | 상품 등록 (판매자) |
| PUT | `/api/v1/product/{productId}` | 상품 수정 |
| PATCH | `/api/v1/product/{productId}/items/{itemId}/status` | 상품 아이템 상태 변경 |

---

# 8. Market 모듈

## 8.1 모듈 개요

**Market**은 쇼핑 플로우의 핵심 서비스입니다. 장바구니, 주문, 쿠폰, 위시리스트를 관리합니다. Outbox 패턴을 사용하여 주문 관련 이벤트를 Kafka로 안전하게 발행하며, Cash/Payout/Product 서비스와 이벤트 기반으로 연동됩니다.

## 8.2 기술 스택

- **Spring Data JPA** + PostgreSQL
- **Apache Kafka** (Outbox 패턴 기반 이벤트 발행)
- **OpenFeign** (Product 서비스 호출)
- **QueryDSL** (복잡한 주문 조회)
- **MapStruct** (DTO 변환)

## 8.3 핵심 도메인 모델

### 8.3.1 주요 엔티티

| 엔티티 | 설명 |
|---|---|
| **Cart** | 유저별 장바구니 (1:1) |
| **CartItem** | 장바구니 상품 아이템 |
| **Order** | 주문 (상태: PENDING→PAID→CONFIRMED/CANCELED) |
| **OrderItem** | 주문 아이템 (판매자별, 가격, 수량) |
| **Coupon** | 쿠폰 (FIXED/PERCENTAGE, 판매자/관리자 발행) |
| **UserCoupon** | 유저가 다운로드한 쿠폰 |
| **MarketUser/MarketSeller** | Replica 유저/판매자 엔티티 |

### 8.3.2 주문 상태 머신

```
PENDING (주문 생성) → PAID (결제 완료) → CONFIRMED (구매 확정)
                   → FAILED (결제 실패)
                   → CANCELED (주문 취소)
                   → EXPIRED (결제 시간 초과)
```

## 8.4 핵심 비즈니스 로직

### 8.4.1 장바구니 관리

- 회원가입 시 `UserJoinedEvent` 수신 → 자동으로 Cart 생성 (`CartEventListener`)
- 상품 추가/수량 변경/삭제

### 8.4.2 주문 생성 → 결제 → 구매 확정 흐름

```
1. 주문 생성 (PENDING) — 쿠폰 적용, 최종 가격 계산
2. 결제 완료 → PAID 상태 변경
3. 구매 확정 → CONFIRMED 상태 변경
   → OrderPurchaseConfirmedEvent 발행 (Outbox)
   → Payout 서비스: 정산 후보 생성
4. 주문 취소 → CANCELED 상태 변경
   → OrderCancelEvent 발행 → Cash: 환불
   → OrderStockReturnEvent 발행 → Product: 재고 복원
```

### 8.4.3 쿠폰 시스템

- **관리자 쿠폰**: `CreateAdminCouponUseCase` — 플랫폼 전체 쿠폰
- **판매자 쿠폰**: `CreateSellerCouponUseCase` — 특정 판매자 쿠폰
- **쿠폰 다운로드**: `DownloadCouponUseCase`
- **쿠폰 적용**: `GetApplicableCouponsUseCase` — 주문 시 적용 가능 쿠폰 조회
- **쿠폰 복원**: `RestoreCouponsUseCase` — 주문 취소 시 쿠폰 복원

## 8.5 MSA 통신

### Kafka 이벤트 발행 (Outbox 패턴)

| 이벤트 | 토픽 | 트리거 |
|---|---|---|
| `OrderPurchaseConfirmedEvent` | order.purchase.confirmed | 구매 확정 |
| `OrderCancelEvent` | order.cancel | 주문 취소 |
| `OrderStockReturnEvent` | order.stock.return | 주문 취소 시 재고 복원 |
| `PaymentCashRefundEvent` | payment.refund | 예치금 환불 |

### Kafka 이벤트 소비

| 토픽 | 소비 로직 |
|---|---|
| `user.joined` | 장바구니 자동 생성, MarketUser 동기화 |
| `seller.joined` | MarketSeller 동기화 |

---

# 9. Cash 모듈

## 9.1 모듈 개요

**Cash**는 예치금(지갑) 관리 서비스입니다. 유저/판매자 지갑 생성, 잔액 충전/차감, 결제, 환불, 거래 내역(CashLog) 관리를 담당합니다. 결제 시스템(토스페이먼츠 연동), 홀딩(임시 보관) 기능을 포함합니다.

## 9.2 기술 스택

- **Spring Data JPA** + PostgreSQL
- **Apache Kafka** (이벤트 소비)
- **MapStruct** (DTO 변환)

## 9.3 핵심 도메인 모델

| 엔티티 | 설명 |
|---|---|
| **CashUser / ReplicaUser** | 유저 복제본 (Kafka sync) |
| **CashSeller / ReplicaSeller** | 판매자 복제본 (Kafka sync) |
| **UserWallet** | 유저 예치금 지갑 (잔액, 홀딩금액) |
| **SellerWallet** | 판매자 수익 지갑 |
| **UserCashLog** | 유저 거래 내역 (충전, 사용, 환불) |
| **SellerCashLog** | 판매자 거래 내역 (정산수령, 수수료, 출금) |
| **CashPolicy** | 정책 상수 (수수료율 등) |

## 9.4 핵심 비즈니스 로직

### 9.4.1 유저 지갑 생성 (Kafka 연동)

```
user.joined 토픽 수신
→ CashSyncUserUseCase: CashUser + UserWallet 생성 (잔액 0)
```

### 9.4.2 결제 흐름

```
1. 잔액 확인 (CashGetBalanceUseCase)
2. 잔액 차감 (CashDeductUserBalanceUseCase) + UserCashLog(사용__주문결제) 기록
3. 홀딩 처리 (CashHoldingUseCase) — 판매자에게 바로 전달하지 않고 시스템이 보관
```

### 9.4.3 환불/정산 흐름

```
payment.refund 토픽 수신 → 유저 잔액 복원 + UserCashLog(환불__결제취소)
payout.wallet.credit 토픽 수신 → 판매자 잔액 입금 + SellerCashLog(정산수령__상품판매_대금)
```

## 9.5 MSA 통신

### Kafka 이벤트 소비

| 토픽 | 소비 로직 |
|---|---|
| `user.joined` | 유저 지갑 생성 |
| `seller.joined` | 판매자 지갑 생성 |
| `payment.refund` | 유저 환불 처리 |
| `payout.wallet.credit` | 판매자 정산금 입금 |

---

# 10. Payout 모듈

## 10.1 모듈 개요

**Payout**은 정산 서비스입니다. 구매 확정 시 판매자에게 지급할 대금, 플랫폼 수수료, **탄소배출량 기반 기부금**을 계산하여 정산 후보를 생성하고, 배치로 확정/지급합니다. 친환경 커머스 컨셉의 핵심 비즈니스 로직이 이 모듈에 있습니다.

## 10.2 기술 스택

- **Spring Data JPA** + PostgreSQL
- **Apache Kafka** (구매 확정 이벤트 소비, 지갑 입금 이벤트 발행)
- **MapStruct** (DTO 변환)
- **배치 처리** (일일 정산 스케줄러)

## 10.3 핵심 도메인 모델

| 엔티티 | 설명 |
|---|---|
| **PayoutCandidateItem** | 정산 후보 항목 (구매 확정 시 생성) |
| **PayoutItem** | 확정된 정산 항목 |
| **Payout** | 정산 건 (판매자별 일별 집계) |
| **DonationLog** | 기부 내역 |
| **CarbonCalculator** | 탄소 배출량 계산기 (상품 무게 × 배송 거리) |
| **DistanceCalculator** | 배송 거리 계산기 |
| **PayoutPolicy** | 수수료율 20% 고정 |

## 10.4 핵심 비즈니스 로직

### 10.4.1 구매 확정 → 정산 후보 생성 (`PayoutHandleOrderPurchaseConfirmedUseCase`)

```
order.purchase.confirmed 토픽 수신 →
OrderItem별로:
  1. 탄소 배출량 계산: weight(kg) × distance(km) × 0.1
  2. CarbonGrade 판정 (GRADE_1~10)
  3. 수수료 = 주문금액 × 20%
  4. 기부금 = 수수료 × donationRate (5%~50%)
  5. 판매자 대금 = 주문금액 - 수수료
  6. PayoutCandidateItem 3건 생성:
     - 정산__상품판매_대금 (판매자에게)
     - 정산__상품판매_수수료 (플랫폼에)
     - 정산__상품판매_기부금 (기부처에)
```

### 10.4.2 탄소 등급 기부 시스템 (`CarbonGrade`)

| 등급 | 탄소 배출량 | 수수료 대비 기부율 |
|---|---|---|
| GRADE_1 (S) | 0~0.5kg | 5% (전체 1%) |
| GRADE_2 (A) | 0.5~1kg | 10% (전체 2%) |
| ... | ... | ... |
| GRADE_10 (I) | 60kg~ | 50% (전체 10%) |

탄소 배출량이 적은 친환경 상품일수록 기부율이 낮아지는 구조입니다. 즉, 탄소 배출이 많은 상품은 더 많은 기부금이 발생합니다.

### 10.4.3 일일 정산 배치 (`PayoutDailyPayoutToWalletUseCase`)

```
1. 확정된 PayoutItem들을 판매자별로 집계
2. PayoutSellerWalletCreditEvent 발행 → Cash 서비스로 입금 요청
3. Payout 상태를 COMPLETED로 변경
```

## 10.5 MSA 통신

### Kafka 이벤트 소비

| 토픽 | 소비 로직 |
|---|---|
| `user.joined` | Payout 유저 동기화 |
| `seller.joined` | Payout 판매자 동기화 |
| `order.purchase.confirmed` | 정산 후보 생성 |
| `order.refunded` | 환불 정산 처리 |

### Kafka 이벤트 발행

| 이벤트 | 토픽 | 용도 |
|---|---|---|
| `PayoutSellerWalletCreditEvent` | payout.wallet.credit | 판매자 지갑 입금 요청 |

---

# 11. Review 모듈

## 11.1 모듈 개요

**Review**는 상품 리뷰 서비스입니다. 구매 확정된 상품에 대해 리뷰 작성이 가능하며, 구매 확정 시 자동으로 리뷰 작성 가능 항목(`ReviewableItem`)이 생성됩니다.

## 11.2 기술 스택

- **Spring Data JPA** + PostgreSQL
- **Apache Kafka** (구매 확정 이벤트 소비)

## 11.3 핵심 도메인 모델

| 엔티티 | 설명 |
|---|---|
| **Review** | 리뷰 (별점, 내용, 이미지) |
| **ReviewableItem** | 리뷰 작성 가능 항목 (구매 확정 시 생성) |

## 11.4 핵심 비즈니스 로직

### 11.4.1 리뷰 작성 가능 항목 생성

```
order.purchase.confirmed 토픽 수신
→ CreateReviewableItemUseCase: 주문 아이템별 ReviewableItem 생성
```

### 11.4.2 리뷰 작성 (`WriteReviewUseCase`)

```
1. ReviewableItem 존재 확인 (구매 확정된 상품만)
2. Review 엔티티 생성 (별점 1~5, 내용, 이미지)
3. ReviewableItem 상태를 REVIEWED로 변경
```

## 11.5 MSA 통신

### Kafka 이벤트 소비

| 토픽 | 소비 로직 |
|---|---|
| `order.purchase.confirmed` | 리뷰 작성 가능 항목 자동 생성 |

### REST API 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| POST | `/api/v1/review` | 리뷰 작성 |
| GET | `/api/v1/review/product/{productId}` | 상품별 리뷰 조회 |

---

# 12. AI 모듈

## 12.1 모듈 개요

**AI**는 OpenAI API 기반의 **AI 추천 서비스**입니다. 상품 등록 시 텍스트를 1536차원 벡터로 임베딩하여 pgvector에 저장하고, 벡터 유사도 검색으로 상품 추천, 자연어 챗봇 추천을 제공합니다. **리액티브 스택(WebFlux + R2DBC)**으로 구현되어 비동기 논블로킹으로 동작합니다.

## 12.2 기술 스택

- **Spring WebFlux** (리액티브 웹)
- **Spring Data R2DBC** + PostgreSQL (논블로킹 DB)
- **pgvector** (벡터 유사도 검색, `<=>` 코사인 거리 연산자)
- **Spring AI** (OpenAI 통합, text-embedding-3-small, gpt-4o-mini)
- **OpenFeign** (Product 서비스 호출)
- **MapStruct** (DTO 변환)

## 12.3 핵심 도메인 모델

### 12.3.1 RecommendItem 엔티티

```java
@Table("recommend_item")
public class RecommendItem extends BaseManualIdAndTime {
    private Long productId;       // 상품 ID
    private String content;       // 상품명 + 설명 (검색 대상 텍스트)
    private String vectorData;    // 1536차원 벡터 (pgvector)
    private BigDecimal price;
    private ProductStatus status;
}
```

### 12.3.2 정책 상수

```java
public class RecommendPolicy {
    public static final int SIMILAR_ITEMS_TOP_N = 50;   // 유사 상품 추천 수
    public static final int CHAT_RECOMMEND_TOP_N = 5;    // 챗봇 추천 수
}
```

## 12.4 핵심 비즈니스 로직

### 12.4.1 상품 임베딩 동기화 (`RecommendSyncItemUseCase`)

```
ProductCreatedEvent 수신 →
1. 상품명 + 카테고리 + 설명 + 옵션을 하나의 content 문자열로 조합
2. OpenAI text-embedding-3-small 모델로 1536차원 벡터 생성
3. RecommendItem 엔티티로 pgvector DB에 저장
```

### 12.4.2 벡터 유사도 기반 상품 추천 (`RecommendSearchItemsUseCase`)

```sql
-- pgvector 코사인 유사도 검색 쿼리
SELECT ri.product_id
FROM recommend_item ri
WHERE ri.product_id != :productId
ORDER BY ri.vector_data <=> (
    SELECT vector_data FROM recommend_item WHERE product_id = :productId
)
LIMIT 50
```

`<=>` 연산자는 pgvector의 **코사인 거리** 연산자입니다. 값이 작을수록 유사합니다.

### 12.4.3 AI 챗봇 추천 (`RecommendFacade.chatRecommend`)

```
1. 유저 질문 텍스트를 OpenAI로 벡터 임베딩
2. pgvector에서 유사 상품 5개 검색
3. Product 서비스에서 상품 상세 조회 (Feign)
4. GPT-4o-mini로 각 상품별 추천 사유 생성
5. {상품 정보 + AI 추천 사유} 응답
```

### 12.4.4 AI 추천 사유 생성 프롬프트

```
사용자 질문: {query}
추천 상품 목록:
[상품 ID: 1] 상품명: ..., 카테고리: ..., 가격: ...원
...
위 상품들에 대해 사용자의 질문과 관련된 추천 사유를 각 상품별로 한 문장으로 작성해주세요.
반드시 JSON 형식으로만 응답: {"상품ID": "추천 사유", ...}
```

## 12.5 MSA 통신

### Spring 내부 이벤트 소비 (@TransactionalEventListener)

| 이벤트 | 소비 로직 |
|---|---|
| `ProductCreatedEvent` | 상품 벡터 임베딩 생성 및 저장 |
| `ProductUpdatedEvent` | 상품 벡터 업데이트 (내용 변경 시 재임베딩) |

### Feign Client 호출 (AI → Product)

| 메서드 | 용도 |
|---|---|
| `getProductDetails(List<Long>)` | 추천 상품 상세 정보 조회 |
| `filterByReviews(List<Long>)` | 리뷰 기반 필터링된 상품 조회 |

### REST API 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/recommendations/{productId}` | 유사 상품 추천 (벡터 유사도) |
| POST | `/api/v1/recommendations/chat` | AI 챗봇 추천 (자연어 → 벡터 → GPT 사유) |

---

# 부록: Kafka 이벤트 흐름 전체 맵

```
                    ┌── market (장바구니 생성)
user.joined ────────┼── cash (유저 지갑 생성)
                    └── payout (정산 유저 동기화)

                    ┌── cash (판매자 지갑 생성)
seller.joined ──────┤
                    └── payout (판매자 동기화)

                    ┌── payout (정산 후보 생성 + 기부금 계산)
order.purchase      │
.confirmed ─────────┤
                    └── review (리뷰 작성 가능 항목 생성)

order.cancel ───────── cash (유저 환불)

order.stock.return ─── product (재고 복원)

payment.refund ─────── cash (예치금 환불)

payout.wallet
.credit ────────────── cash (판매자 정산금 입금)
```

---

*이 문서는 MOSSY 프로젝트 코드베이스를 기반으로 자동 생성되었습니다.*

