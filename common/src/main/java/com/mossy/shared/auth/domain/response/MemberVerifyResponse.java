package com.mossy.shared.auth.domain.response;

import com.mossy.shared.member.domain.role.RoleCode;

import java.util.List;

public record MemberVerifyResponse(
        Long userId,
        List<RoleCode> roles,
        boolean isValid // 비밀번호 일치 여부

) {}
