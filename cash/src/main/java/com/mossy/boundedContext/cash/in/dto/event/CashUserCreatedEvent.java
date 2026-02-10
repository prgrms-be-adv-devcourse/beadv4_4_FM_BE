package com.mossy.boundedContext.cash.in.dto.event;

import com.mossy.shared.member.payload.UserPayload;

public record CashUserCreatedEvent(
    UserPayload user
) {
}