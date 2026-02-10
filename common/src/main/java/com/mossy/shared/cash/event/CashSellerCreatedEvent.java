package com.mossy.shared.cash.event;

import com.mossy.shared.member.payload.SellerPayload;

public record CashSellerCreatedEvent(
    SellerPayload seller
) {}
