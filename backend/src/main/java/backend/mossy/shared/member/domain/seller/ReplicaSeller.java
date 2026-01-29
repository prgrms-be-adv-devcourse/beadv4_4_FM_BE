package backend.mossy.shared.member.domain.seller;

import backend.mossy.shared.member.domain.user.BaseUser;
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
                status
        );
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

