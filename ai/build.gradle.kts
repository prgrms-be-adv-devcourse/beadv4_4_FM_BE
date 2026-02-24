plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.mossy"
version = "0.0.1-SNAPSHOT"

val springCloudVersion = "2024.0.0"
val springAiVersion = "1.0.0-M5"

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
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
    }
}

dependencies {
    // common 모듈
    implementation(project(":common"))
    implementation(project(":kafka"))

    // WebFlux (논블로킹/리액티브)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // OpenFeign (blocking HTTP client for inter-service calls)
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // R2DBC (PostgreSQL)
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    // Spring AI (OpenAI)
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")

    // 롬복
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // MapStruct
    implementation ("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor ("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Swagger UI (WebFlux)
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.3")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

springBoot {
    mainClass.set("com.mossy.RecommendationApplication")
}

tasks.bootJar {
    enabled = true
    archiveFileName.set("recommendation.jar")
}

tasks.jar {
    enabled = false
}