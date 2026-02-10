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

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
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
    // common 모듈
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("org.postgresql:postgresql")

    // OpenFeign (Market 서비스와 통신)
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // 롬복
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3")

    // MapStruct
    implementation ("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor ("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // QueryDSL
//    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
//    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
//    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

springBoot {
    mainClass.set("com.mossy.CashApplication")
}

tasks.bootJar {
    enabled = true
    archiveFileName.set("cash.jar")
}

tasks.jar {
    enabled = true
}
