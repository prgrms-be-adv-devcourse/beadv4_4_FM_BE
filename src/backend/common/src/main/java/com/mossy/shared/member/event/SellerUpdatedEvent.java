package com.mossy.shared.member.event;

import com.mossy.shared.member.dto.event.SellerApprovedEvent;

public record SellerUpdatedEvent(
        SellerApprovedEvent seller
) {

}
