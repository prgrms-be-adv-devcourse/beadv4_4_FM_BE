package com.mossy.boundedContext.donation.in.dto.response;

import com.mossy.boundedContext.donation.domain.DonationLog;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record DonationLogResponseDto(
        Long id,
        Long orderItemId,
        BigDecimal amount,
        BigDecimal carbonOffset,
        LocalDateTime createdAt
) {
    public static DonationLogResponseDto from(DonationLog log) {
        return DonationLogResponseDto.builder()
                .id(log.getId())
                .orderItemId(log.getOrderItemId())
                .amount(log.getAmount())
                .carbonOffset(log.getCarbonOffset())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
