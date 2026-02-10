package com.mossy.shared.payout.event;
import com.mossy.shared.member.payload.UserPayload;
public record PayoutUserCreatedEvent(UserPayload user) {
}
