plugins {
    `java-library`
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.mossy"
version = "0.0.1-SNAPSHOT"
description = "common"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // 공통 라이브러리
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-data-redis")

    compileOnly("org.springframework.boot:spring-boot-starter-web")

    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // DB 및 롬복
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    //Swagger
    api("org.springdoc:springdoc-openapi-starter-common:2.8.3")

    //JWT
    api("io.jsonwebtoken:jjwt-api:0.12.6")
    api("org.springframework.security:spring-security-core")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // 테스트 (공통)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Library module - disable bootJar, enable jar
tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
