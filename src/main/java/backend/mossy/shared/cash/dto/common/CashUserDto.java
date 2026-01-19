package backend.mossy.shared.cash.dto.common;

import backend.mossy.boundedContext.cash.domain.user.CashUser;
import backend.mossy.shared.member.domain.user.UserStatus;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record CashUserDto(
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
    public static CashUserDto from(CashUser user) {
        return CashUserDto.builder()
            .id(user.getId())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .email(user.getEmail())
            .name(user.getName())
            .nickname(user.getNickname())
            .profileImage(user.getProfileImage())
            .build();
    }
}