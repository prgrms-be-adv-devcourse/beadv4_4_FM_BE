package backend.mossy.shared.payout.dto.response;

import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import java.time.LocalDateTime;

/**
 * 정산 응답에 포함될 멤버의 간략한 정보를 담는 DTO
 */
public record PayoutUserResponseDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String username,
        String nickname
) {
    public static PayoutUserResponseDto from(PayoutSeller user) {
        return new PayoutUserResponseDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getName(),
                user.getNickname()
        );
    }
}