package com.mossy.boundedContext.payout.in.dto.command;

import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.shared.cash.enums.SellerEventType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PayoutCandidateItemCreateDto(
        LocalDateTime paymentDate,
        PayoutCandidateCreateDto orderItem,
        SellerEventType eventType,
        PayoutUser payer,
        PayoutSeller payee,
        BigDecimal amount,
        String weightGrade,
        BigDecimal deliveryDistance,
        BigDecimal carbonKg
) {
}
