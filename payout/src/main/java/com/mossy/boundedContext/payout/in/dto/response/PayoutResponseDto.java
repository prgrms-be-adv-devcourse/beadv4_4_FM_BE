package com.mossy.boundedContext.payout.in.dto.response;

import com.mossy.boundedContext.payout.domain.payout.Payout;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PayoutResponseDto {
    private final Long id;
    private final String status;
    private final BigDecimal amount;
    private final LocalDateTime payoutDate;
    private final LocalDateTime creditDate;

    public static PayoutResponseDto from(Payout payout) {
        String status = payout.isCredited() ? "CREDITED" : "COMPLETED";
        return PayoutResponseDto.builder()
                .id(payout.getId())
                .status(status)
                .amount(payout.getAmount())
                .payoutDate(payout.getPayoutDate())
                .creditDate(payout.getCreditDate())
                .build();
    }
}
