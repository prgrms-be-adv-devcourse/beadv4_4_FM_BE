package com.mossy.shared.payout.event;

import com.mossy.shared.member.dto.event.SellerPayload;

public record PayoutSellerCreatedEvent(SellerPayload seller) {
}
