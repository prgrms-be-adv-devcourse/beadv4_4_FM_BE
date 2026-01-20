package backend.mossy.shared.payout.event;

import backend.mossy.shared.member.dto.event.SellerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PayoutSellerCreatedEvent {
    private final SellerDto seller;
}
