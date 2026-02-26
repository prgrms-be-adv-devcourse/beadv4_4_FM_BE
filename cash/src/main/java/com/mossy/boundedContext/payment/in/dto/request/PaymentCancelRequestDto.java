package com.mossy.boundedContext.payment.in.dto.request;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PaymentCancelRequestDto(
    @NotBlank String orderId,
    @NotBlank String cancelReason,
    BigDecimal cancelAmount,
    List<Long> ids
) {
    public boolean isPartialCancel() {
        return ids != null && !ids.isEmpty();
    }
}