package backend.mossy.shared.payout.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 개별 정산 항목의 정보를 응답하는 DTO
 */
@Builder
public record PayoutItemResponseDto(
        Long payoutItemId,
        String eventType,
        BigDecimal amount,
        LocalDateTime itemPayDate
) {
}