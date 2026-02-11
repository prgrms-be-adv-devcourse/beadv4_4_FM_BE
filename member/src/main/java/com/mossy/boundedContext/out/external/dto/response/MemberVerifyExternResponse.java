package com.mossy.boundedContext.out.external.dto.response;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.shared.member.domain.role.RoleCode;

import java.util.List;
import java.util.stream.Collectors;

public record MemberVerifyExternResponse(
        Long userId,
        List<RoleCode> roles,
        boolean isValid // 비밀번호 일치 여부

) {
    public static MemberVerifyExternResponse of(User user, boolean isValid) {
        List<RoleCode> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode())
                .collect(Collectors.toList());

        return new MemberVerifyExternResponse(user.getId(), roles, isValid);
    }
}
