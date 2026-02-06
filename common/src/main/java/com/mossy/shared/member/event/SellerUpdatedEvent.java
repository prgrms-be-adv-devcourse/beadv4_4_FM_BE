package com.mossy.shared.member.event;

import com.mossy.shared.member.payload.SellerPayload;

public record SellerUpdatedEvent(
        SellerPayload seller
) {

}
