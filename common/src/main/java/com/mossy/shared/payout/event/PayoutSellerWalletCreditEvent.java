package com.mossy.shared.payout.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 판매자 지갑 입금 이벤트
 * 하루에 판매자당 1개 발행 (여러 Payout 합산)
 */
@Builder
public record PayoutSellerWalletCreditEvent(
    Long sellerId,       // 판매자 ID
    BigDecimal amount,   // 입금 금액 (오늘 지급된 모든 Payout 합산)
    LocalDate creditDate // 지급 일자
)
{
}
