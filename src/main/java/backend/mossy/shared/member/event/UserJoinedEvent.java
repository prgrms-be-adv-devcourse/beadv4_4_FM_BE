package backend.mossy.shared.member.event;

import backend.mossy.shared.member.dto.common.UserDto;

public record UserJoinedEvent(
    UserDto user
) {

}
