package backend.mossy.shared.payout.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 개별 정산 항목의 정보를 응답하는 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class PayoutItemResponseDto {
    private Long payoutItemId;
    private String eventType;
    private BigDecimal amount;
    private LocalDateTime itemPayDate;
}
