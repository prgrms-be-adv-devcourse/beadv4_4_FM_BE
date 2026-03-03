package com.mossy.shared.member.event;

import com.mossy.shared.member.payload.SellerPayload;
import lombok.Builder;

@Builder
public record SellerJoinedEvent(
        SellerPayload seller
){
}
