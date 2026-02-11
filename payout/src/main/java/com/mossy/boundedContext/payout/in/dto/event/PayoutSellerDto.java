package com.mossy.boundedContext.payout.in.dto.event;

import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import com.mossy.shared.member.payload.SellerPayload;
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
    public static PayoutSellerDto from(SellerPayload seller) {
        return PayoutSellerDto.builder()
                .sellerId(seller.sellerId())
                .userId(seller.userId())
                .sellerType(seller.sellerType())
                .storeName(seller.storeName())
                .businessNum(seller.businessNum())
                .latitude(seller.latitude())
                .longitude(seller.longitude())
                .status(seller.status())
                .createdAt(seller.createdAt())
                .updatedAt(seller.updatedAt())
                .build();
    }
}
