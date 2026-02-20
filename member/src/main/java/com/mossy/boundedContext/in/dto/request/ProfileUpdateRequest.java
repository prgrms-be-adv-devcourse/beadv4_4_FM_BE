package com.mossy.boundedContext.in.dto.request;

public record ProfileUpdateRequest(
        String phoneNum,
        String address,
        String rrn,
        String nickname
) {
}
