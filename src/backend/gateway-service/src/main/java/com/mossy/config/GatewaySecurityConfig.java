package com.mossy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                // 1. CSRF, FormLogin, HttpBasic 비활성화 (Stateless API)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                // 2. 세션을 사용하지 않도록 설정 (JWT 사용 환경)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                // 3. 경로별 권한 설정
                .authorizeExchange(exchanges -> exchanges
                        // 인증이 필요 없는 경로 (로그인, 회원가입, Swagger 등)
                        .pathMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/product/products/**",
                                "/api/v1/product/search/**",
                                "/mossy-docs/**",
                                "/v3/api-docs/**"

                        ).permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyExchange().authenticated()
                )
                .build();
    }
}