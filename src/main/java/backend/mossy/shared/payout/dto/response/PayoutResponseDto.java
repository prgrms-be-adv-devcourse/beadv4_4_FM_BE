package backend.mossy.shared.payout.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 단일 정산의 상세 정보를 응답하는 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class PayoutResponseDto {
    private Long payoutId;
    private LocalDateTime createdDate;
    private LocalDateTime payoutDate; // 정산 완료일
    private PayoutMemberResponseDto payee; // 정산받는 사람 정보
    private BigDecimal totalAmount;
    private List<PayoutItemResponseDto> items; // 정산 항목 상세
}
