package com.mossy.boundedContext.payout.in.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PayoutListResponseDto(
        Summary summary,
        List<PayoutResponseDto> payouts
) {
    public record Summary(
            BigDecimal totalAmount,
            BigDecimal creditedAmount,
            BigDecimal pendingCreditAmount
    ) {}
}
