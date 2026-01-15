package backend.mossy.shared.payout.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 단일 정산의 상세 정보를 응답하는 DTO
 */
@Builder
public record PayoutResponseDto(
        Long payoutId,
        LocalDateTime createdDate,
        LocalDateTime payoutDate, // 정산 완료일
        PayoutMemberResponseDto payee, // 정산받는 사람 정보
        BigDecimal totalAmount,
        List<PayoutItemResponseDto> items // 정산 항목 상세
) {
}