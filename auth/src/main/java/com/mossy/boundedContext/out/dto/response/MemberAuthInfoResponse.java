package com.mossy.boundedContext.out.dto.response;

import com.mossy.shared.member.domain.role.RoleCode;

import java.util.List;

public record MemberAuthInfoResponse(
        Long userId,
        List<RoleCode> roles,
        Long sellerId,
        boolean active

) {
}
