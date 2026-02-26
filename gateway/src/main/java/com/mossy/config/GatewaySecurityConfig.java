package com.mossy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity // WebFlux 전용 보안 어노테이션
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .cors(Customizer.withDefaults())
                // 1. CSRF, FormLogin, HttpBasic 비활성화 (Stateless API)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(form -> form.disable())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                // 2. 세션을 사용하지 않도록 설정 (JWT 사용 환경)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                // 3. 경로별 권한 설정
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/internal/**").denyAll() // 내부 서비스 간 통신은 게이트웨이에서 차단
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().permitAll()
                )
                .build();
    }
}