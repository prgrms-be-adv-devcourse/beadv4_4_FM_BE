package com.mossy.boundedContext.app.mapper;

import com.mossy.boundedContext.out.dto.OAuth2UserDTO;
import com.mossy.boundedContext.out.dto.OAuth2UserInfo;
import com.mossy.boundedContext.out.dto.response.SocialLonginResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OAuth2UserMapper {

    public OAuth2UserDTO toDTO(OAuth2UserInfo userInfo) {
        return new OAuth2UserDTO(
                userInfo.providerId(),
                userInfo.provider(),
                userInfo.email(),
                userInfo.name()
        );
    }

    public OAuth2UserDTO toDTO(SocialLonginResponse response) {
        return new OAuth2UserDTO(
                String.valueOf(response.id()),
                null, // provider 정보는 SocialLoginResponse에 없음
                response.email(),
                response.name()
        );
    }

    public SocialLonginResponse toSocialLoginResponse(Long id, String email, String name, List<String> roles, boolean isNewUser) {
        return new SocialLonginResponse(
                id,
                email,
                name,
                roles,
                isNewUser
        );
    }

    public SocialLonginResponse toSocialLoginResponse(OAuth2UserDTO userDTO, Long id, List<String> roles, boolean isNewUser) {
        return new SocialLonginResponse(
                id,
                userDTO.email(),
                userDTO.name(),
                roles,
                isNewUser
        );
    }
}

