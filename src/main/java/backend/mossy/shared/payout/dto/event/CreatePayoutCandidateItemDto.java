package backend.mossy.shared.payout.dto.event;

import backend.mossy.boundedContext.payout.domain.payout.PayoutEventType;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CreatePayoutCandidateItemDto(
        PayoutEventType eventType,
        String relTypeCode,
        Long relId,
        LocalDateTime paymentDate,
        PayoutSeller payer,
        PayoutSeller payee,
        BigDecimal amount
) {
}
