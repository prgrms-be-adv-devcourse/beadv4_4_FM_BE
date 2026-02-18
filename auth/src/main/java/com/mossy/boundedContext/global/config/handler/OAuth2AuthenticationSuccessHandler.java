package com.mossy.boundedContext.global.config.handler;

import com.mossy.boundedContext.app.AuthFacade;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthFacade authFacade;

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
        OAuth2UserDTO userDTO = OAuth2UserDTO.from(userInfo);

        log.debug("OAuth2 사용자 정보: provider={}, email={}, name={}",
                userInfo.provider(), userInfo.email(), userInfo.name());

        try {
            // AuthFacade를 통해 사용자 정보 저장/업데이트 및 토큰 발급
            var loginResponse = authFacade.upsertUserAndIssueToken(userDTO);

            // 프론트엔드로 토큰 전달
            String redirectUrl = String.format("%s/auth/callback?accessToken=%s&refreshToken=%s",
                    frontendUrl,
                    URLEncoder.encode(loginResponse.accessToken(), StandardCharsets.UTF_8),
                    URLEncoder.encode(loginResponse.refreshToken(), StandardCharsets.UTF_8));

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 처리 실패");
        }
    }
}



