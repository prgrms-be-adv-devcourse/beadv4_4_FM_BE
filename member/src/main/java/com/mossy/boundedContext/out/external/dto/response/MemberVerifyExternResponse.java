package com.mossy.boundedContext.out.external.dto.response;

import com.mossy.shared.member.domain.role.RoleCode;

import java.util.List;

public record MemberVerifyExternResponse(
        Long userId,
        List<RoleCode> roles,
        boolean isValid,
        Long sellerId // SELLER 권한이 있을 때만 값이 있음
) {
}
