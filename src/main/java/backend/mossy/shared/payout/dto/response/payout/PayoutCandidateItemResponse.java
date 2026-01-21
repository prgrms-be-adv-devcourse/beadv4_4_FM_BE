package backend.mossy.shared.payout.dto.response.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.payout.PayoutEventType;
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
    public static PayoutCandidateItemResponse from(PayoutCandidateItem item) {
        return PayoutCandidateItemResponse.builder()
                .id(item.getId())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .eventType(item.getEventType())
                .relTypeCode(item.getRelTypeCode())
                .relId(item.getRelId())
                .paymentDate(item.getPaymentDate())
                .payerId(item.getPayer() != null ? item.getPayer().getId() : null)
                .payerName(item.getPayer() != null ? item.getPayer().getStoreName() : null)
                .payeeId(item.getPayee().getId())
                .payeeName(item.getPayee().getStoreName())
                .amount(item.getAmount())
                .payoutItemId(item.getPayoutItem() != null ? item.getPayoutItem().getId() : null)
                .build();
    }
}
