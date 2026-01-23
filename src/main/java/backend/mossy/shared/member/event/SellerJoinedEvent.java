package backend.mossy.shared.member.event;

import backend.mossy.shared.member.dto.event.SellerApprovedEvent;

public record SellerJoinedEvent(
        SellerApprovedEvent seller
){}
