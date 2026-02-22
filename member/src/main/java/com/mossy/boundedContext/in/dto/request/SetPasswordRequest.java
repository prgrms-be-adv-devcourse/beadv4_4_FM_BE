package com.mossy.boundedContext.in.dto.request;

import jakarta.validation.constraints.NotBlank;

// 소셜 로그인 전용 계정 → 최초 비밀번호 설정용
public record SetPasswordRequest(
        @NotBlank(message = "설정할 비밀번호를 입력해주세요.")
        String newPassword
) {
}

