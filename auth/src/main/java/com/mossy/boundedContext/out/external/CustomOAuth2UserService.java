package com.mossy.boundedContext.out.external;

import com.mossy.boundedContext.app.AuthFacade;
import com.mossy.shared.member.domain.entity.BaseUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AuthFacade authFacade;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        log.info("OAuth2 사용자 정보 로드 시작");

        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("OAuth2 속성: {}", oAuth2User.getAttributes());

        // OAuth2User 객체 반환 (SecurityContext에 저장됨)
        // 실제 사용자 저장/업데이트는 OAuth2AuthenticationSuccessHandler에서 처리
        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                oAuth2User.getAttributes(),
                "email"
        );
    }
}
