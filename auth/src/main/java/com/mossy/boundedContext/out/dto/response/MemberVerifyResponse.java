package com.mossy.boundedContext.out.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mossy.shared.member.domain.role.RoleCode;

import java.util.List;

public record MemberVerifyResponse(
        Long userId,
        List<RoleCode> roles,
        boolean isValid,
        @JsonProperty("sellerId") Long sellerId
) {}
