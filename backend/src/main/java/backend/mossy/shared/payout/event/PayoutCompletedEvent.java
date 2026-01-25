package backend.mossy.shared.payout.event;

import backend.mossy.shared.payout.dto.event.payout.PayoutEventDto;

public record PayoutCompletedEvent(PayoutEventDto payout) {
}
