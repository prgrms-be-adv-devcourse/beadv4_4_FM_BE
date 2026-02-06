package com.mossy.boundedContext.cash.domain.seller;

import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
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
@Table(name = "CASH_SELLER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "seller_id"))
public class CashSeller extends ReplicaSeller {

    @Builder
    public CashSeller(
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
        super(
            id,
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

    public void update(SellerType sellerType, String storeName, String businessNum,
        BigDecimal latitude, BigDecimal longitude, SellerStatus status) {
        this.sellerType = sellerType;
        this.storeName = storeName;
        this.businessNum = businessNum;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }
}