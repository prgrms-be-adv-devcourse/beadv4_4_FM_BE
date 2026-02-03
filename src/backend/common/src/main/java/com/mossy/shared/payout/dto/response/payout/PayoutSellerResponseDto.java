package com.mossy.shared.payout.dto.response.payout;

import com.mossy.shared.member.domain.seller.SellerStatus;
import com.mossy.shared.member.domain.seller.SellerType;

import java.time.LocalDateTime;

/**
 * 정산 응답에 포함될 판매자의 간략한 정보를 담는 DTO
 */
public record PayoutSellerResponseDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long userId,
        SellerType sellerType,
        String storeName,
        SellerStatus status
) {
}