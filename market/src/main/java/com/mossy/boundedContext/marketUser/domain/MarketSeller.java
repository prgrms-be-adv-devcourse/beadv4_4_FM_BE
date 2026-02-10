package com.mossy.boundedContext.marketUser.domain;

import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import com.mossy.shared.member.payload.SellerPayload;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MARKET_SELLER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "seller_id"))
public class MarketSeller extends ReplicaSeller {
    @Builder
    public MarketSeller(
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
        super(
                sellerId,
                userId,
                sellerType,
                storeName,
                businessNum,
                latitude,
                longitude,
                status,
                createdAt,
                updatedAt
        );
    }

    public static MarketSeller from(SellerPayload seller) {
        return MarketSeller.builder()
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

    public void updateSeller(SellerPayload seller) {
        super.changeSeller(seller);
    }
}