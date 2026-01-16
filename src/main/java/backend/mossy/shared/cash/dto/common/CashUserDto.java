package backend.mossy.shared.cash.dto.common;

import backend.mossy.boundedContext.cash.domain.wallet.CashUser;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record CashUserDto(
    Long id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String email,
    String name,
    String nickname,
    String profileImage
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