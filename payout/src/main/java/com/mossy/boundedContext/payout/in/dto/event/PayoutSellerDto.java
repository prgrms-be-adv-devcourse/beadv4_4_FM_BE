package com.mossy.boundedContext.payout.in.dto.event;

import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PayoutSellerDto(
        Long sellerId,
        Long userId,
        SellerType sellerType,
        String storeName,
        String businessNum,
        BigDecimal latitude,
        BigDecimal longitude,
        SellerStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
