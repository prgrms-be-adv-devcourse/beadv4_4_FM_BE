package com.mossy.boundedContext.domain.seller;

import com.mossy.shared.member.domain.entity.BaseSeller;
import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "seller_id"))
public abstract class SourceSeller extends BaseSeller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SourceSeller(
            Long userId,
            SellerType sellerType,
            String storeName,
            String businessNum,
            BigDecimal latitude,
            BigDecimal longitude,
            SellerStatus status,
            String profileImageUrl
    ) {
        super(
                userId, sellerType, storeName, businessNum,
                latitude, longitude, status, profileImageUrl
        );
    }
}
