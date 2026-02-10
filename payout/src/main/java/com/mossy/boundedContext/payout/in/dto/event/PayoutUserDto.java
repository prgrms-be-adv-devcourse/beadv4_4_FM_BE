package com.mossy.boundedContext.payout.in.dto.event;


import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.payload.UserPayload;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PayoutUserDto(
        Long id,
        String email,
        String name,
        String address,
        String nickname,
        BigDecimal latitude,
        BigDecimal longitude,
        String profileImage,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    /**
     * UserPayload로부터 PayoutUserCreateDto를 생성하는 정적 팩토리 메서드
     */
    public static PayoutUserDto from(UserPayload user) {
        return new PayoutUserDto(
                user.id(), user.email(), user.name(), user.address(),
                user.nickname(), user.latitude(), user.longitude(),
                user.profileImage(),user.status(), user.createdAt(), user.updatedAt()

        );
    }
}
