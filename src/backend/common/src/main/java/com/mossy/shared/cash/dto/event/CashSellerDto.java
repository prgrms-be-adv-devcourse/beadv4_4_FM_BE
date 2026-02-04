package com.mossy.shared.cash.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CashSellerDto(
    Long id,
    Long userId,
    String sellerType,
    String storeName,
    String businessNum,
    String representativeName,
    String contactEmail,
    String contactPhone,
    String address1,
    String address2,
    BigDecimal latitude,
    BigDecimal longitude,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}