package backend.mossy.shared.market.dto.event;

import backend.mossy.shared.member.domain.user.UserStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record MarketUserDto(
    Long id,
    String email,
    String name,
    String address,
    String nickname,
    String profileImage,
    BigDecimal longitude,
    BigDecimal latitude,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}