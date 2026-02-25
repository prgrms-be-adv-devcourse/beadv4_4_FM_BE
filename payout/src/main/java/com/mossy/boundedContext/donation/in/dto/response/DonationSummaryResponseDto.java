package com.mossy.boundedContext.donation.in.dto.response;

import com.mossy.boundedContext.donation.domain.DonationLog;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record DonationSummaryResponseDto(
        int year,
        int month,
        BigDecimal totalAmount,
        BigDecimal totalCarbonOffset,
        int donationCount
) {
    public static DonationSummaryResponseDto from(int year, int month, List<DonationLog> logs) {
        BigDecimal totalAmount = logs.stream()
                .map(DonationLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCarbonOffset = logs.stream()
                .map(DonationLog::getCarbonOffset)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return DonationSummaryResponseDto.builder()
                .year(year)
                .month(month)
                .totalAmount(totalAmount)
                .totalCarbonOffset(totalCarbonOffset)
                .donationCount(logs.size())
                .build();
    }
}
