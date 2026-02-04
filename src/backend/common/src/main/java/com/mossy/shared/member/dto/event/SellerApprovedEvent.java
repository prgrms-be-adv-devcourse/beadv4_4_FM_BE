package com.mossy.shared.member.dto.event;

import com.mossy.member.domain.seller.SellerStatus;
import com.mossy.member.domain.seller.SellerType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SellerApprovedEvent(
        Long id,
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
