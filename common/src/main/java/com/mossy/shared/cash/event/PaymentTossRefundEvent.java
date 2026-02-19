package com.mossy.shared.cash.event;

import com.mossy.shared.cash.payload.TossCancelPayload;

public record PaymentTossRefundEvent(
    TossCancelPayload response
) {

}
