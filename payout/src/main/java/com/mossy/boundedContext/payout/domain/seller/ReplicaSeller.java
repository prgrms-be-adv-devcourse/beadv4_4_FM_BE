package com.mossy.boundedContext.payout.domain.seller;

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

    /**
     * Member 컨텍스트로부터 받은 판매자 정보로 현재 엔티티의 필드를 업데이트
     * JPA 더티 체킹을 통해 실제 변경된 필드만 DB에 반영됨
     *
     * @param seller Member 컨텍스트에서 전달된 판매자 정보 DTO
     */
    protected void changeSeller(SellerPayload seller) {
        this.id = seller.sellerId();
        this.userId = seller.userId();
        this.sellerType = seller.sellerType();
        this.storeName = seller.storeName();
        this.businessNum = seller.businessNum();
        this.latitude = seller.latitude();
        this.longitude = seller.longitude();
        this.status = seller.status();
        this.createdAt = seller.createdAt();
        this.updatedAt = seller.updatedAt();
    }
}

