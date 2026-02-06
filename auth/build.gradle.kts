plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.mossy"
version = "0.0.1-SNAPSHOT"

val springCloudVersion = "2024.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    implementation(project(":common")) // [공통 모듈] RsData, Exception 등 포함

    // 1. Web & Security (인증 모듈의 핵심)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // 2. JWT (이게 있어야 JwtProvider의 빨간 줄이 사라집니다!)
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // 3. OpenFeign (Member 서비스와 통신)
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // 4. Redis (Refresh Token 및 로그인 실패 횟수 저장)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // 5. Swagger (API 문서화)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3")

    // 6. Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // 7. Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

springBoot {
    mainClass.set("com.mossy.auth.AuthApplication")
}