package com.mossy.boundedContext.app.mapper;

import com.mossy.boundedContext.out.dto.OAuth2UserDTO;
import com.mossy.boundedContext.out.dto.OAuth2UserInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    default OAuth2UserDTO toOAuth2UserDTO(OAuth2UserInfo userInfo, Long linkUserId) {
        if (userInfo == null) return null;
        return new OAuth2UserDTO(
                userInfo.providerId(),
                userInfo.provider(),
                userInfo.email(),
                userInfo.name(),
                linkUserId
        );
    }
}

