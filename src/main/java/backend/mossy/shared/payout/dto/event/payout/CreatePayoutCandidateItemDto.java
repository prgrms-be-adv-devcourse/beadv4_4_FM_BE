package backend.mossy.shared.payout.dto.event.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.boundedContext.payout.domain.payout.PayoutEventType;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
