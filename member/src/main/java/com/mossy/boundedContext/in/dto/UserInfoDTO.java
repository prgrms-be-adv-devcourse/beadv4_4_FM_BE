package com.mossy.boundedContext.in.dto;

import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDTO {
    private Long userId;
    private String nickname;
    private String username;
    private SellerRequestStatus status;
}
