package com.mossy.boundedContext.cash.in.dto.request;

import com.mossy.shared.cash.enums.PayMethod;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CashRefundRequestDto(
    Long orderId,
    Long buyerId,
    BigDecimal amount,
    PayMethod payMethod
) {
}
