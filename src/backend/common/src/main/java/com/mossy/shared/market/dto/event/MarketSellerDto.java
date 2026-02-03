package com.mossy.shared.market.dto.event;

import com.mossy.shared.member.domain.seller.SellerStatus;
import com.mossy.shared.member.domain.seller.SellerType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record MarketSellerDto (
        Long sellerId,
        Long userId,
        SellerType sellerType,
        String storeName,
        String businessNum,
        String representativeName,
        String contactEmail,
        String contactPhone,
        String address1,
        String address2,
        BigDecimal latitude,
        BigDecimal longitude,
        SellerStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){ }
