package com.mossy.shared.member.event;

import com.mossy.shared.member.dto.event.UserDto;

public record UserUpdatedEvent(
        UserDto user
) { }
