package com.mossy.shared.payout.event;

import com.mossy.shared.cash.enums.SellerEventType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 판매자 지갑 입금 이벤트
 * 하루에 판매자당 타입별 1개 발행 (여러 Payout 합산)
 */
@Builder
public record PayoutSellerWalletCreditEvent(
    Long sellerId,              // 판매자 ID
    BigDecimal amount,          // 입금 금액 (타입별 합산)
    SellerEventType eventType,  // 정산 사유
    LocalDate creditDate        // 지급 일자
)
{
}
