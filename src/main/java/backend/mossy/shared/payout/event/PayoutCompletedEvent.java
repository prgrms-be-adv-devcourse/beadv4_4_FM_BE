package backend.mossy.shared.payout.event;

import backend.mossy.shared.payout.dto.event.PayoutEventDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PayoutCompletedEvent {
    private final PayoutEventDto payout;
}
