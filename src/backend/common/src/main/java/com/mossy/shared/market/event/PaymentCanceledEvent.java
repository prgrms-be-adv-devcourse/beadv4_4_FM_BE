package com.mossy.shared.market.event;

import com.mossy.shared.market.dto.toss.TossCancelResponse;

public record PaymentCanceledEvent(
    TossCancelResponse response
) {
}
