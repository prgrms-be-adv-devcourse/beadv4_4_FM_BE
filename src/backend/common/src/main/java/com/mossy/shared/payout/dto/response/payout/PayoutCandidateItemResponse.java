package com.mossy.shared.payout.dto.response.payout;


import com.mossy.shared.payout.enums.PayoutEventType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payout 후보 항목에 대한 응답 데이터 전송 객체(DTO)
 */
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
