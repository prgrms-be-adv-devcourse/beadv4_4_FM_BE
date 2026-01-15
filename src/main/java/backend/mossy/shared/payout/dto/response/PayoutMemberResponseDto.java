package backend.mossy.shared.payout.dto.response;

import backend.mossy.boundedContext.payout.domain.PayoutMember;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 정산 응답에 포함될 멤버의 간략한 정보를 담는 DTO
 */
@Builder // 빌더 어노테이션 추가
public record PayoutMemberResponseDto(
        Long id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String username,
        String nickname,
        int activityScore
) {
    public static PayoutMemberResponseDto from(PayoutMember member) {
        return PayoutMemberResponseDto.builder()
                .id(member.getId())
                .createDate(member.getCreateDate())
                .modifyDate(member.getModifyDate())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .activityScore(member.getActivityScore())
                .build();
    }
}