package backend.mossy.shared.market.event;

import backend.mossy.shared.market.dto.toss.TossCancelResponse;

public record PaymentCanceledEvent(
    TossCancelResponse response
) {
}
