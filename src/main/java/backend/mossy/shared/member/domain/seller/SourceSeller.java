package backend.mossy.shared.member.domain.seller;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "SELLER",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_sellers_user_id", columnNames = "user_id")
        }
)
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SourceSeller extends BaseSeller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public SourceSeller(Long userId, SellerType sellerType, String storeName, String businessNum, String representativeName, String contactEmail, String contactPhone, String address1, String address2, SellerStatus status, Long id, Long userId1, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(userId, sellerType, storeName, businessNum, representativeName, contactEmail, contactPhone, address1, address2, status);
        this.id = id;
        this.userId = userId1;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isApproved() {
        return this.status == SellerStatus.APPROVED;
    }
}
