package backend.mossy.shared.market.dto;

import backend.mossy.shared.member.domain.user.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MarketUserDto (
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
) { }
