package backend.mossy.shared.member.event;

import backend.mossy.shared.payout.dto.event.SellerDto;

public record SellerUpdatedEvent(
        SellerDto seller
) {

}
