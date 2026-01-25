package backend.mossy.shared.payout.dto.event.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.boundedContext.payout.domain.payout.PayoutEventType;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payout 후보 항목 생성을 위한 데이터 전송 객체(DTO)
 * 이 DTO는 Payout 생성 이벤트에 사용
 */
@Builder
public record CreatePayoutCandidateItemDto(
        PayoutEventType eventType,
        String relTypeCode,
        Long relId,
        LocalDateTime paymentDate,
        PayoutUser payer,
        PayoutSeller payee,
        BigDecimal amount
) {
}
