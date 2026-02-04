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

dependencies {
    // common 모듈 (JWT 검증 로직을 공유하기 위해 필요)
    implementation(project(":common")) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
    }

    // API Gateway
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")

    // Eureka Client (나중에 서비스 검색 시 필요)
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // Gateway는 전용 보안 로직이 필요하므로 아래 추가
    implementation("org.springframework.boot:spring-boot-starter-security")

    // 롬복
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


springBoot {
    mainClass.set("com.mossy.GatewayApplication")
}

tasks.bootJar {
    enabled = true
    archiveFileName.set("gateway-service.jar")
}

tasks.jar {
    enabled = true
}
