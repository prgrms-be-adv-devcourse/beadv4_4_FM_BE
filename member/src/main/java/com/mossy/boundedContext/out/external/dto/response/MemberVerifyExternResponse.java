package com.mossy.boundedContext.out.external.dto.response;

import com.mossy.shared.member.domain.role.RoleCode;

import java.util.List;

public record MemberVerifyExternResponse(
        Long userId,
        List<RoleCode> roles,
        boolean isValid
) {
}
