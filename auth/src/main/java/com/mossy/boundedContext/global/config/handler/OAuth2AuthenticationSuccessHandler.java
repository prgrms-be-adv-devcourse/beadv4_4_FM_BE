package com.mossy.boundedContext.global.config.handler;

import com.mossy.boundedContext.app.AuthFacade;
import com.mossy.boundedContext.app.mapper.AuthMapper;
import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.out.dto.OAuth2UserDTO;
import com.mossy.boundedContext.out.dto.OAuth2UserInfo;
import com.mossy.boundedContext.out.dto.OAuth2UserInfoImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.core.env.Environment;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7일

    private final AuthFacade authFacade;
    private final AuthMapper mapper;
    private final Environment environment;
    private final com.mossy.boundedContext.global.jwt.JwtProvider jwtProvider;

    @Value("${spring.security.oauth2.frontendUrl:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException {
        log.info("OAuth2 인증 성공");

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = authToken.getPrincipal();
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        OAuth2UserInfo userInfo = new OAuth2UserInfoImpl(oAuth2User.getAttributes(), registrationId);
        
        Long linkUserId = extractLinkUserIdFromCookie(request, response);
        OAuth2UserDTO userDTO = mapper.toOAuth2UserDTO(userInfo, linkUserId);

        log.debug("OAuth2 사용자 정보: provider={}, email={}, name={}, linkUserId={}",
                userInfo.provider(), userInfo.email(), userInfo.name(), linkUserId);

        try {
            LoginResponse loginResponse = authFacade.upsertUserAndIssueToken(userDTO);

            addRefreshTokenCookie(response, loginResponse.refreshToken());

            String redirectUrl = buildRedirectUrl(loginResponse);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            String errorRedirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                    .path("/login")
                    .queryParam("status", "error")
                    .build()
                    .toUriString();
            getRedirectStrategy().sendRedirect(request, response, errorRedirectUrl);
        }
    }

    //RefreshToken을 HttpOnly 쿠키로 추가
    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(isProductionEnvironment());
        cookie.setPath("/");
        cookie.setMaxAge(REFRESH_TOKEN_COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    //프론트엔드 리다이렉트 URL 생성
    private String buildRedirectUrl(LoginResponse loginResponse) {
        return UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/auth/callback")
                .queryParam("accessToken", loginResponse.accessToken())
                .queryParam("isNewUser", loginResponse.isNewUser())
                .build()
                .toUriString();
    }

    //운영 환경 여부 확인
    private boolean isProductionEnvironment() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    //연동 토큰 추출 및 삭제
    private Long extractLinkUserIdFromCookie(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("linkToken".equals(cookie.getName())) {
                try {
                    String token = cookie.getValue();
                    Long userId = jwtProvider.getUserId(token);
                    
                    // 쿠키 삭제
                    Cookie deleteCookie = new Cookie("linkToken", null);
                    deleteCookie.setMaxAge(0);
                    deleteCookie.setPath("/");
                    response.addCookie(deleteCookie);
                    
                    return userId;
                } catch (Exception e) {
                    log.warn("Failed to parse linkToken cookie: {}", e.getMessage());
                }
            }
        }
        return null;
    }
}
