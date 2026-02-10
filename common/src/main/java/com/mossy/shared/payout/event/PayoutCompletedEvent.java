package com.mossy.shared.payout.event;

import com.mossy.shared.payout.payload.PayoutEventDto;

public record PayoutCompletedEvent(PayoutEventDto payout) {
}
