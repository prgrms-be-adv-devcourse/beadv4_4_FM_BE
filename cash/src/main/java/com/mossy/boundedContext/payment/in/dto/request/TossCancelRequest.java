package com.mossy.boundedContext.payment.in.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

public record TossCancelRequest(
    String cancelReason,
    @JsonInclude(JsonInclude.Include.NON_NULL) BigDecimal cancelAmount
) {
    public TossCancelRequest(String cancelReason) {
        this(cancelReason, null);
    }
}
