# 1. 빌드 스테이지
FROM gradle:jdk25 AS builder
WORKDIR /app

# 컨텍스트를 src/backend로 잡을 것이므로, 그 위치의 설정 파일들을 복사합니다.
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 실행 권한 부여
RUN chmod +x ./gradlew

# 모든 모듈 소스 복사 (gateway-service 포함)
# 컨텍스트가 src/backend이므로 현재 폴더 전체를 복사하면 됩니다.
COPY . .

# gateway-service 빌드
RUN ./gradlew :gateway-service:bootJar -x test --no-daemon

# 2. 실행 스테이지
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

# 타임존 설정
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 빌드된 JAR 파일 복사
COPY --from=builder /app/gateway-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:+UseG1GC", \
            "-Dspring.profiles.active=prod", \
            "-jar", "app.jar"]