package backend.mossy.shared.payout.event;

import backend.mossy.shared.member.dto.event.SellerApprovedEvent;


public record PayoutSellerCreatedEvent(SellerApprovedEvent seller) {
}
