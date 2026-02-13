package com.mossy.boundedContext.donation.in.dto.command;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateDonationLogDto(
        Long OrderItemId,
        Long buyerId,
        BigDecimal donationAmount,
        BigDecimal carbonKg
) {

}