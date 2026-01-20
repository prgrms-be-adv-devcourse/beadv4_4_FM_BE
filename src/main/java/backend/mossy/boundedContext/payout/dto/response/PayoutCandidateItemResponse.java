package backend.mossy.boundedContext.payout.dto.response;

import backend.mossy.boundedContext.payout.domain.PayoutEventType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PayoutCandidateItemResponse(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        PayoutEventType eventType,
        String relTypeCode,
        Long relId,
        LocalDateTime paymentDate,
        Long payerId,
        String payerName,
        Long payeeId,
        String payeeName,
        BigDecimal amount,
        Long payoutItemId
) {
}
