package com.mossy.shared.payout.payload;


import com.mossy.shared.cash.enums.SellerEventType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payout 후보 항목 생성을 위한 데이터 전송 객체(DTO)
 * 이 DTO는 Payout 생성 이벤트에 사용
 */
@Builder
public record CreatePayoutCandidateItemDto(
        SellerEventType eventType,
        String relTypeCode,
        Long relId,
        LocalDateTime paymentDate,
        Long payerId,
        Long payeeId,
        BigDecimal amount
) {
}
