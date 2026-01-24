# 첫 번째 스테이지: 빌드 스테이지

FROM gradle:jdk21-graal-jammy as builder


# 작업 디렉토리 설정

WORKDIR /app


# 소스 코드와 Gradle 래퍼 복사

COPY gradlew .

COPY gradle gradle

COPY build.gradle.kts .

COPY settings.gradle.kts .



# Gradle 래퍼에 실행 권한 부여

RUN chmod +x ./gradlew



# 종속성 설치

RUN ./gradlew dependencies --no-daemon



# 소스 코드 복사

COPY src src



# 애플리케이션 빌드

RUN ./gradlew build -x test --no-daemon



# 두 번째 스테이지: 실행 스테이지

FROM ghcr.io/graalvm/jdk-community:21



# 작업 디렉토리 설정

WORKDIR /app



# 타임존 설정

ENV TZ=Asia/Seoul

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone



# 첫 번째 스테이지에서 빌드된 JAR 파일 복사

COPY --from=builder /app/build/libs/*.jar app.jar



# 실행할 JAR 파일 지정

ENTRYPOINT ["java", "-XX:+UseContainerSupport",
"-XX:MaxRAMPercentage=75.0",
"-XX:+UseG1GC",
"-XX:MaxGCPauseMillis=200",
"-XX:+UseStringDeduplication",
"-Dspring.profiles.active=prod",
"-jar", "app.jar"]