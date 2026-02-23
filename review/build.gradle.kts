plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.mossy"
version = "0.0.1-SNAPSHOT"

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
    // common 모듈
    implementation(project(":common"))
    // kafka 모듈
    implementation(project(":kafka"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.kafka:spring-kafka")
    runtimeOnly("org.postgresql:postgresql")

    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3")

    // AWS S3
    implementation(platform("software.amazon.awssdk:bom:2.24.0"))
    implementation("software.amazon.awssdk:s3")

    // 컴파일 및 런타임 도구
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // QueryDSL QClass 생성을 위한 프로세서들
    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//Q클래스 생성 경로
sourceSets {
    main {
        java {
            srcDir("build/generated/sources/annotationProcessor/java/main")
        }
    }
}

springBoot {
    mainClass.set("com.mossy.ReviewApplication")
}

tasks.bootJar {
    enabled = true
    archiveFileName.set("review.jar")
}

tasks.jar {
    enabled = true
}
