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

val springCloudVersion = "2024.0.0"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    // 공통
    implementation(project(":common"))
    implementation(project(":kafka"))

    // 스프링 이벤트 재시도 라이브러리
    implementation("org.springframework.retry:spring-retry")

    // Spring Batch
    implementation("org.springframework.boot:spring-boot-starter-batch")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // QueryDSL (JPA 사용 시 공통으로 필요)
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3")

    // Spring Boot Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // 컴파일 도구 및 DB 드라이버
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val generatedDir = "build/generated/sources/annotationProcessor/java/main"

sourceSets {
    main {
        java {
            srcDirs(generatedDir)
        }
    }
}

// 빌드 시 생성된 폴더를 깨끗이 비우고 시작하도록 추가하면 꼬임 방지에 좋습니다.
tasks.clean {
    delete(generatedDir)
}

springBoot {
    mainClass.set("com.mossy.MarketApplication")
}

tasks.bootJar {
    enabled = true
    archiveFileName.set("market.jar")
}

tasks.jar {
    enabled = true
}
