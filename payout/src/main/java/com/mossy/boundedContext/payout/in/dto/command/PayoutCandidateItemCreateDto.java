package com.mossy.boundedContext.payout.in.dto.command;

import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.shared.payout.enums.PayoutEventType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PayoutCandidateItemCreateDto(
        LocalDateTime paymentDate,
        CreatePayoutCandidateDto orderItem,
        PayoutEventType eventType,
        PayoutUser payer,
        PayoutSeller payee,
        BigDecimal amount,
        String weightGrade,
        BigDecimal deliveryDistance
) {
    public static PayoutCandidateItemCreateDto of(
            LocalDateTime paymentDate, CreatePayoutCandidateDto orderItem, PayoutEventType eventType,
            PayoutUser payer, PayoutSeller payee, BigDecimal amount,
            String weightGrade, BigDecimal deliveryDistance
    ) {
        return new PayoutCandidateItemCreateDto(
                paymentDate, orderItem, eventType, payer, payee, amount, weightGrade, deliveryDistance
        );
    }

}
