package com.mossy.shared.cash.dto.request;

import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.shared.cash.enums.UserEventType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserBalanceRequestDto(
        Long userId,
        BigDecimal amount,
        UserEventType eventType,
        String relTypeCode,
        Long relId) {

    public UserBalanceRequestDto withUserId(Long userId) {
        return UserBalanceRequestDto.builder()
                .userId(userId)
                .amount(amount)
                .eventType(eventType)
                .relTypeCode(relTypeCode)
                .relId(relId)
                .build();
    }

    public UserBalanceRequestDto {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCode.AMOUNT_MUST_BE_POSITIVE);
        }
    }
}