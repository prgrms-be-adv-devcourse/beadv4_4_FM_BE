package com.mossy.boundedContext.cash.in.dto.event;

import com.mossy.shared.member.payload.SellerPayload;

public record CashSellerCreatedEvent(
    SellerPayload seller
) {}
