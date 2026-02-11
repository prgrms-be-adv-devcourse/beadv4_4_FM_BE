package com.mossy.boundedContext.payment.in.dto.event;

import com.mossy.boundedContext.payment.in.dto.response.TossCancelResponse;

public record PaymentCanceledEvent(
    TossCancelResponse response
) {

}
