package backend.mossy.shared.payout.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 정산 응답에 포함될 멤버의 간략한 정보를 담는 DTO
 */
@Getter
@AllArgsConstructor
public class PayoutMemberResponseDto {
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private String username;
    private String nickname;
    private int activityScore;
}
