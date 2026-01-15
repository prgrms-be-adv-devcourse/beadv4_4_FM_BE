package backend.mossy.shared.payout.dto.response;

import java.time.LocalDateTime;

/**
 * 정산 응답에 포함될 멤버의 간략한 정보를 담는 DTO
 */
public record PayoutMemberResponseDto(
        Long id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String username,
        String nickname,
        int activityScore
) {
}