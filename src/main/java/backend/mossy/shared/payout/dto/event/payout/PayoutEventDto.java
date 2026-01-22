package backend.mossy.shared.payout.dto.event.payout;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payout 이벤트를 위한 데이터 전송 객체(DTO)입니다.
 * 이 DTO는 Payout 이벤트 발생 시 관련 데이터를 전달하는 데 사용됩니다.
 */
@Builder
public record PayoutEventDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long payeeId,
        String payeeNickname,
        LocalDateTime payoutDate,
        BigDecimal amount,
        boolean isSystem
) {

}
