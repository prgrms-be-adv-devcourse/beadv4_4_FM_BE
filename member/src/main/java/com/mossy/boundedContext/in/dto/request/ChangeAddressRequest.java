package com.mossy.boundedContext.in.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeAddressRequest(
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        String currentPassword,

        @NotBlank(message = "주소를 입력해주세요.")
        String address
) {
}

