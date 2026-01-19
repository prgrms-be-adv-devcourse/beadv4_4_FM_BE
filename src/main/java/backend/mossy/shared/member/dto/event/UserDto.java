package backend.mossy.shared.member.dto.event;

import backend.mossy.shared.member.domain.user.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDto(
    Long id,
    String email,
    String name,
    String address,
    String nickname,
    String profileImage,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}