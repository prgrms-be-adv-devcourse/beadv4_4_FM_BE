package com.mossy.shared.payout.event;

import com.mossy.shared.payout.dto.event.payout.PayoutEventDto;

public record PayoutCompletedEvent(PayoutEventDto payout) {
}
