package com.mossy.boundedContext.app.mapper;

import com.mossy.boundedContext.out.dto.OAuth2UserDTO;
import com.mossy.boundedContext.out.dto.OAuth2UserInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    // --- [OAuth2 DTO 변환] ---

    /**
     * OAuth2UserInfo(interface)는 MapStruct 자동 매핑 불가 → default 메서드 사용
     */
    default OAuth2UserDTO toOAuth2UserDTO(OAuth2UserInfo userInfo) {
        if (userInfo == null) return null;
        return new OAuth2UserDTO(
                userInfo.providerId(),
                userInfo.provider(),
                userInfo.email(),
                userInfo.name()
        );
    }
}

