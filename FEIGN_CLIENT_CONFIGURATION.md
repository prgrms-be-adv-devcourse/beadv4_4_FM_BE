# FeignClient URL Configuration Guide

## 📋 개요
모든 FeignClient는 환경별(로컬/배포) 설정 기반으로 변경되었습니다. 하드코딩된 URL이 없습니다.

---

## 🎯 FeignClient 목록 및 설정

### 1. **auth ↔️ member 통신**

#### AuthFeignClient (member에서 auth 호출)
- **설정 키**: `${mossy.feign.url}`
- **로컬**: `http://localhost:8086`
- **배포**: `http://auth:8086`

#### MemberFeignClient (auth에서 member 호출)
- **설정 키**: `${mossy.feign.url}`
- **로컬**: `http://localhost:8082`
- **배포**: `http://member:8082`

---

### 2. **market ↔️ product 통신**

#### CouponFeignClient (market → product)
#### CartFeignClient (market → product)
#### WishlistFeignClient (market → product)
#### OrderFeignClient (market → product)

- **설정 키**: `${mossy.feign.product-url}`
- **로컬**: `http://localhost:8090`
- **배포**: `http://product:8090`

---

### 3. **ai → product 통신**

#### ProductFeignClient (ai에서 product 호출)
- **설정 키**: `${mossy.feign.product-url}`
- **로컬**: `http://localhost:8090`
- **배포**: `http://product:8090`

---

### 4. **cash ↔️ market 통신**

#### MarketFeignClient (cash에서 market 호출)
- **설정 키**: `${mossy.feign.market-url}`
- **로컬**: `http://localhost:8081`
- **배포**: `http://market:8081`

---

## 📝 각 모듈별 application.yml 설정

### auth/src/main/resources/application.yml
```yaml
mossy:
  feign:
    url: http://localhost:8082  # 로컬 개발 환경
```

### auth/src/main/resources/application-prod.yml
```yaml
mossy:
  feign:
    url: http://member:8082  # 배포 환경 (Docker)
```

---

### member/src/main/resources/application.yml
```yaml
mossy:
  feign:
    url: http://localhost:8086  # 로컬 개발 환경
```

### member/src/main/resources/application-prod.yml
```yaml
mossy:
  feign:
    url: http://auth:8086  # 배포 환경 (Docker)
```

---

### market/src/main/resources/application.yml
```yaml
mossy:
  feign:
    product-url: http://localhost:8090  # 로컬 개발 환경
```

### market/src/main/resources/application-prod.yml
```yaml
mossy:
  feign:
    product-url: http://product:8090  # 배포 환경 (Docker)
```

---

### ai/src/main/resources/application.yml
```yaml
mossy:
  feign:
    product-url: http://localhost:8090  # 로컬 개발 환경
```

### ai/src/main/resources/application-prod.yml
```yaml
mossy:
  feign:
    product-url: http://product:8090  # 배포 환경 (Docker)
```

---

### cash/src/main/resources/application.yml
```yaml
mossy:
  feign:
    market-url: http://localhost:8081  # 로컬 개발 환경
```

### cash/src/main/resources/application-prod.yml
```yaml
mossy:
  feign:
    market-url: http://market:8081  # 배포 환경 (Docker)
```

---

## 🚀 실행 방법

### 로컬 개발 환경 (기본값)
```bash
# 각 모듈별로 실행
./gradlew :auth:bootRun
./gradlew :member:bootRun
./gradlew :market:bootRun
./gradlew :product:bootRun
./gradlew :ai:bootRun
./gradlew :cash:bootRun
./gradlew :gateway:bootRun
```

### Docker 배포 환경
```bash
# Docker Compose 사용 (자동으로 application-prod.yml 로드)
docker-compose up

# 또는 Java 실행 시
java -jar app.jar --spring.profiles.active=prod
```

---

## ✅ 체크리스트

- [x] AuthFeignClient 설정 변경
- [x] MemberFeignClient 설정 변경
- [x] CouponFeignClient 설정 변경
- [x] CartFeignClient 설정 변경
- [x] WishlistFeignClient 설정 변경
- [x] OrderFeignClient 설정 변경
- [x] ProductFeignClient (ai) 설정 변경
- [x] MarketFeignClient (cash) 설정 변경
- [x] 모든 application.yml 파일 업데이트
- [x] 모든 application-prod.yml 파일 업데이트
- [x] Gateway 라우팅 설정 업데이트

---

## 📌 주의사항

1. **환경 변수 설정**: Docker 배포 시 환경 변수가 제대로 설정되어야 합니다.
2. **프로파일 활성화**: 배포 시 `--spring.profiles.active=prod` 또는 `SPRING_PROFILES_ACTIVE=prod` 환경 변수 필수
3. **Docker 네트워크**: Docker Compose 배포 시 모든 서비스가 동일한 네트워크에 있어야 합니다.
4. **포트 바인딩**: 로컬 개발 환경에서는 각 서비스의 포트가 localhost에 바인딩되어 있어야 합니다.

