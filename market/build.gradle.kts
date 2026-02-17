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
    // 공통
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    //implementation("org.springframework.boot:spring-boot-starter-batch")

    // Market 서비스 전용 라이브러리
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation(platform("software.amazon.awssdk:bom:2.24.0"))
    implementation("software.amazon.awssdk:s3")

    // QueryDSL (JPA 사용 시 공통으로 필요)
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3")

    // MapStruct
    implementation ("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor ("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor ("org.projectlombok:lombok-mapstruct-binding:0.2.0")

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
