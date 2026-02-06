package com.mossy.boundedContext.cash.in.dto.request;

import com.mossy.shared.cash.enums.PayMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CashHoldingRequestDto(
    Long orderId,
    Long buyerId,
    LocalDateTime paymentDate,
    BigDecimal amount,
    PayMethod payMethod
) {

}
