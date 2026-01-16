package backend.mossy.shared.payout.dto.response;

import backend.mossy.boundedContext.payout.domain.PayoutUser;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 정산 응답에 포함될 멤버의 간략한 정보를 담는 DTO
 */
@Builder // 빌더 어노테이션 추가
public record PayoutUserResponseDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String username,
        String nickname
) {
    public static PayoutUserResponseDto from(PayoutUser user) {
        return PayoutUserResponseDto.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .username(user.getName())
                .nickname(user.getNickname())
                .build();
    }
}