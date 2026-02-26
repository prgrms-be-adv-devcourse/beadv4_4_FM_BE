package com.mossy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // 1. Gateway에서 글로벌 CORS 설정 적용
                .cors(ServerHttpSecurity.CorsSpec::disable)

                // 2. CSRF, FormLogin, HttpBasic 비활성화
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(form -> form.disable())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                // 3. 경로별 권한 설정
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/internal/**").denyAll()
                        .pathMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/v1/seller/**").hasRole("SELLER")
                        .anyExchange().permitAll()
                )
                .build();
    }

    // CORS 정책 정의 (WebFlux 환경용)
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // 실제 프론트엔드 도메인 추가 (localhost도 개발용으로 남겨둠)
//        configuration.setAllowedOrigins(Arrays.asList(
//                "http://mossy-eco.biz",
//                "http://localhost:5173",
//                "http://localhost:3000"
//        ));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        configuration.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}