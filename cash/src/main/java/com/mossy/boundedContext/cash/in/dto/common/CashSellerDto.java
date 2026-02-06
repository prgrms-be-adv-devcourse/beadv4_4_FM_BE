package com.mossy.boundedContext.cash.in.dto.common;

import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CashSellerDto(
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