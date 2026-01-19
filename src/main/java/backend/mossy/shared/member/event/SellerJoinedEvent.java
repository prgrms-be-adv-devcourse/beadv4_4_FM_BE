package backend.mossy.shared.member.event;

import backend.mossy.shared.payout.dto.event.SellerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellerJoinedEvent {
    private final SellerDto seller;
}