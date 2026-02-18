FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# 현재 폴더에 있는 파일들을 바로 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .

RUN printf 'rootProject.name = "backend"\n' > settings.gradle.kts

# 소스 코드 복사 (gateway 폴더가 아닌 현재 위치의 src)
COPY src src

RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]