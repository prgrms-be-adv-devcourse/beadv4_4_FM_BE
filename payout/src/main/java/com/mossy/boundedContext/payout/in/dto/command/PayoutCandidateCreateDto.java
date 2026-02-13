package com.mossy.boundedContext.payout.in.dto.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * [Command DTO] 정산 후보 생성을 위한 내부 DTO
 * OrderPaidEvent로부터 받은 정보와 Payout 도메인에서 계산한 정보를 담아서 전달
 */
@Builder
public record PayoutCandidateCreateDto(
        // OrderItem 기본 정보
        Long orderItemId,
        Long buyerId,
        String buyerName,
        Long sellerId,
        BigDecimal orderPrice,
        LocalDateTime orderItemCreatedAt,
        LocalDateTime orderItemUpdatedAt,
        // Payout 도메인에서 계산/설정한 정보
        String weightGrade,           // 무게 등급 (payout에서 설정)
        BigDecimal deliveryDistance,  // 배송 거리 (payout에서 계산)
        LocalDateTime paymentDate     // 결제 일시
) {
}