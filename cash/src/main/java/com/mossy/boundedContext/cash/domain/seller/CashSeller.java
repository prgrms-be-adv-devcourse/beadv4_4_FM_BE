package com.mossy.boundedContext.cash.domain.seller;

import com.mossy.boundedContext.cash.in.dto.command.CashSellerDto;
import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
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
public class CashSeller extends ReplicaSeller {

    @Builder
    public CashSeller(
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

    public void update(CashSellerDto cashSellerDto) {
        super.update(cashSellerDto);
    }
}