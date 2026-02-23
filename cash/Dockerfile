# ============================================
# 1단계: 빌드 스테이지 (JDK 21 사용)
# ============================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# 1. Gradle 래퍼 및 설정 파일 복사
COPY gradlew .
RUN apk add --no-cache dos2unix && dos2unix gradlew
COPY gradle gradle
# 루트에 build.gradle.kts가 있다면 복사 (없다면 아래 줄을 주석 처리하세요)
#COPY build.gradle.kts .

# 2. 빌드 구조 동적 생성 (cash common만 포함하여 빌드 속도 최적화)
RUN printf 'rootProject.name = "backend"\ninclude("common") \
    \ninclude("kafka") \
    \ninclude("cash")\n' > settings.gradle.kts

# 3. 의존성 캐싱을 위한 build 파일 선복사
RUN mkdir -p common cash
COPY common/build.gradle.kts common/
COPY kafka/build.gradle.kts kafka/
COPY cash/build.gradle.kts cash/

# 의존성 먼저 다운로드 (라이브러리 캐싱 레이어)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# 4. 실제 소스 코드 복사
COPY common/src common/src
COPY kafka/src kafka/src
COPY cash/src cash/src

# 5. JAR 빌드 (테스트 스킵)
RUN ./gradlew :cash:bootJar --no-daemon -x test

# ============================================
# 2단계: 실행 스테이지 (JRE 21 사용 - 경량화 및 보안)
# ============================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV TZ=Asia/Seoul

# 시스템 패키지 업데이트 및 보안 설정 (비루트 사용자)
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# 빌드 결과물만 복사
COPY --from=build /app/cash/build/libs/*.jar app.jar

# 권한 부여 및 사용자 전환
RUN chown appuser:appgroup app.jar
USER appuser

# JVM 최적화 옵션 및 실행
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:+UseG1GC", \
            "-Dspring.profiles.active=prod", \
            "-jar", "app.jar"]