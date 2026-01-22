package backend.mossy.shared.payout.event;

import backend.mossy.shared.member.dto.event.SellerDto;
import lombok.AllArgsConstructor;


public record PayoutSellerCreatedEvent(SellerDto seller) {
}
