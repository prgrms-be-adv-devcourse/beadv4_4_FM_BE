package backend.mossy.shared.member.dto.event;

import backend.mossy.shared.member.domain.user.UserStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserDto(
    Long id,
    String email,
    String name,
    String rrnEncrypted,
    String phoneNum,
    String password,
    String address,
    String nickname,
    String profileImage,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}