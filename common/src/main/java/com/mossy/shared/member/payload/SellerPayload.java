package com.mossy.shared.member.payload;

import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SellerPayload(
        Long sellerId,
        Long userId,
        SellerType sellerType,
        String storeName,
        String businessNum,
        BigDecimal latitude,
        BigDecimal longitude,
        SellerStatus status,
        String profileImageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

}
