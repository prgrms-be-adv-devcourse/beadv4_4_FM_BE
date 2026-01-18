package backend.mossy.shared.member.domain.seller;

import backend.mossy.shared.member.domain.user.BaseUser;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ReplicaSeller extends BaseSeller {
    @Id
    @Column(name = "seller_id", nullable = false)
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReplicaSeller(
            Long id,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long userId,
            SellerType sellerType,
            String storeName,
            String businessNum,
            String representativeName,
            String contactEmail,
            String contactPhone,
            String address1,
            String address2,
            SellerStatus status
            ) {
        super(userId, sellerType, storeName, businessNum, representativeName, contactEmail, contactPhone, address1, address2, status);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

