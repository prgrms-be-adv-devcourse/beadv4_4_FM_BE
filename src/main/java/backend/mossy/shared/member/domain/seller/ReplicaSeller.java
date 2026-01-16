package backend.mossy.shared.member.domain.seller;

import backend.mossy.shared.member.domain.user.BaseUser;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplicaSeller extends BaseSeller {
    @Id
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReplicaSeller(
            Long userId,
            SellerType sellerType,
            String storeName,
            String businessNum,
            String representativeName,
            String contactEmail,
            String contactPhone,
            String address1,
            String address2
    ) {
        super(userId,sellerType, storeName, businessNum, representativeName, contactEmail, contactPhone, address1, address2);

        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

