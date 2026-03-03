package com.mossy.shared.member.event;

import com.mossy.shared.member.payload.UserPayload;

public record UserUpdatedEvent(
        UserPayload user
) { }
