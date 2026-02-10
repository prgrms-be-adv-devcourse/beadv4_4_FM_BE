package com.mossy.shared.cash.event;

import com.mossy.shared.member.payload.UserPayload;

public record CashUserCreatedEvent(
    UserPayload user
) {
}