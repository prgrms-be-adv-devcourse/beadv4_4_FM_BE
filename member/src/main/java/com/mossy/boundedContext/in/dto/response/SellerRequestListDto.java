package com.mossy.boundedContext.in.dto.response;

import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SellerRequestListDto(
        Long id,
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
        SellerRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}


