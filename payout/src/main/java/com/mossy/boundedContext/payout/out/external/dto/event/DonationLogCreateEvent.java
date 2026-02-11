package com.mossy.boundedContext.payout.out.external.dto.event;

import java.math.BigDecimal;

/**
 * [Domain Event] 기부 로그 생성 이벤트
 * 정산 완료 시 기부금 정보를 Donation 도메인에 전달하여 기부 로그를 생성하도록 요청
 */
public record DonationLogCreateEvent(
        Long orderItemId,        // 주문 항목 ID
        Long buyerId,            // 구매자 ID
        BigDecimal donationAmount, // 기부금액
        BigDecimal carbonKg      // 탄소 배출량 (kg)
) {
}