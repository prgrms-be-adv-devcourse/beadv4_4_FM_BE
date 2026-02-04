package com.mossy.member.global.config;

import com.mossy.member.global.security.JwtAuthenticationFilter;
import com.mossy.member.global.security.RestAccessDeniedHandler;
import com.mossy.member.global.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

                //CSRF 비활성
                .csrf(csrf -> csrf.disable())
                //CORS 설정
                .cors(Customizer.withDefaults())
                //세션 인증 방식 비활성, JWT 사용 방식으로 변경
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //폼 로그인 비활성
                .formLogin(form -> form.disable())
                //기본 인증 비활성
                .httpBasic(basic -> basic.disable())
                // 인증 / 인가 예외 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                // swagger & 공통
                                "/mossy-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/error",

                                // Member 서비스 내의 Auth 관련 예시 (토큰 발급 등)
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login",
                                "/api/v1/auth/reissue",
                                "/api/v1/auth/ping"
                        ).permitAll()
                        // member 서비스 관련 API만 명시 (나머지 서비스 경로는 삭제!)
                        .requestMatchers("/api/v1/member/**").authenticated()
                        .anyRequest().authenticated()
                )

                //JWT 인증 필터
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
