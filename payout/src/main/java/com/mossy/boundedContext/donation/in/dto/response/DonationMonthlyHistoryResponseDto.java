package com.mossy.boundedContext.donation.in.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record DonationMonthlyHistoryResponseDto(
        int year,
        int month,
        BigDecimal totalAmount,
        BigDecimal totalCarbonOffset,
        int donationCount,
        List<DonationLogResponseDto> logs
) {
}
