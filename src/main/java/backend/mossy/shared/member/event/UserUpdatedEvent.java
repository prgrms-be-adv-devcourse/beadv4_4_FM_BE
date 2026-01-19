package backend.mossy.shared.member.event;

import backend.mossy.shared.member.dto.event.UserDto;

public record UserUpdatedEvent(
        UserDto user
) { }
