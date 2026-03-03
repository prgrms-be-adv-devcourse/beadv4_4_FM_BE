package com.mossy.boundedContext.marketUser.domain;

import com.mossy.shared.member.domain.entity.BaseSeller;
import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import com.mossy.shared.member.payload.SellerPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ReplicaSeller extends BaseSeller {
    @Id
    @Column(name = "seller_id", nullable = false)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ReplicaSeller(
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
                userId,
                sellerType,
                storeName,
                businessNum,
                latitude,
                longitude,
                status,
                null
        );
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected void changeSeller(SellerPayload seller) {
        this.id = seller.sellerId();
        this.sellerType = seller.sellerType();
        this.storeName = seller.storeName();
        this.businessNum = seller.businessNum();
        this.latitude = seller.latitude();
        this.longitude = seller.longitude();
        this.status = seller.status();
        this.updatedAt = seller.updatedAt();
    }
}

