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
    /**
     * SellerPayload로부터 PayoutSellerDto를 생성하는 정적 팩토리 메서드
     */
    public static PayoutSellerDto from(SellerPayload seller) {
        return new PayoutSellerDto(
                seller.sellerId(),
                seller.userId(),
                seller.sellerType(),
                seller.storeName(),
                seller.businessNum(),
                seller.latitude(),
                seller.longitude(),
                seller.status(),
                seller.createdAt(),
                seller.updatedAt()
        );
    }
}
