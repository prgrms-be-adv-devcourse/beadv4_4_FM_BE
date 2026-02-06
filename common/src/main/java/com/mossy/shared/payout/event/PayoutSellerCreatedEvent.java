package com.mossy.shared.payout.event;

import com.mossy.shared.member.payload.SellerPayload;

public record PayoutSellerCreatedEvent(SellerPayload seller) {
}
