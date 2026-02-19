package com.mossy.boundedContext.global.config.handler;

import com.mossy.boundedContext.app.AuthFacade;
import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.out.dto.OAuth2UserDTO;
import com.mossy.boundedContext.out.dto.OAuth2UserInfo;
import com.mossy.boundedContext.out.dto.OAuth2UserInfoImpl;
import com.mossy.boundedContext.app.mapper.OAuth2UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.core.env.Environment;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthFacade authFacade;
    private final OAuth2UserMapper oAuth2UserMapper;
    private final Environment environment;

    @Value("${spring.security.oauth2.frontendUrl:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException {
        log.info("OAuth2 인증 성공");

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = authToken.getPrincipal();
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // OAuth2UserInfo 생성
        OAuth2UserInfo userInfo = new OAuth2UserInfoImpl(attributes, registrationId);
        OAuth2UserDTO userDTO = oAuth2UserMapper.toDTO(userInfo);

        log.debug("OAuth2 사용자 정보: provider={}, email={}, name={}",
                userInfo.provider(), userInfo.email(), userInfo.name());

        try {
            // AuthFacade를 통해 사용자 정보 저장/업데이트 및 토큰 발급
            LoginResponse loginResponse = authFacade.upsertUserAndIssueToken(userDTO);

            // RefreshToken을 HttpOnly 쿠키에 저장
            Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.refreshToken());
            refreshTokenCookie.setHttpOnly(true);
            // 운영 환경(prod)에서만 Secure 설정 (HTTPS만 허용)
            boolean isProduction = environment.getActiveProfiles().length > 0 &&
                                  environment.getActiveProfiles()[0].equals("prod");
            refreshTokenCookie.setSecure(isProduction);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
            response.addCookie(refreshTokenCookie);

            // AccessToken은 QueryParameter로 전달 (프론트엔드에서 메모리에 저장)
            String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                    .path("/auth/callback")
                    .queryParam("accessToken", loginResponse.accessToken())
                    .queryParam("isNewUser", loginResponse.isNewUser())
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 처리 실패");
        }
    }
}



