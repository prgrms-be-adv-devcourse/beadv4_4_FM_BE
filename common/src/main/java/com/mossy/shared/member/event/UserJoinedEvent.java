package com.mossy.shared.member.event;

import com.mossy.shared.member.payload.UserPayload;

public record UserJoinedEvent(
    UserPayload user
) {

}
