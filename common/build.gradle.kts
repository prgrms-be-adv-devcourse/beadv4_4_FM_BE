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
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // DB 및 롬복
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    //Swagger
    api("org.springdoc:springdoc-openapi-starter-common:2.8.3")

    // 테스트 (공통)
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    //security 공통
    api ("org.springframework.boot:spring-boot-starter-security")
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
