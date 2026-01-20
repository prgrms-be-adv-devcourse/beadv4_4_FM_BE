package backend.mossy.shared.member.event;

import backend.mossy.shared.member.dto.event.SellerDto;

public record SellerJoinedEvent(
    SellerDto seller
){}
