package com.mossy.boundedContext.in.dto;

import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    private Long userId;
    private String nickname;
    private String username;
    private SellerRequestStatus status;

    public static UserInfoDto of(Long userId, String nickname, String username,SellerRequestStatus status) {
        return new UserInfoDto(userId, nickname, username, status);
    }
}
