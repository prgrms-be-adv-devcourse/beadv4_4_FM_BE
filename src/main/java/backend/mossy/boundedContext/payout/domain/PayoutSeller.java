package backend.mossy.boundedContext.payout.domain;

import backend.mossy.shared.member.domain.seller.ReplicaSeller;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.payout.dto.event.SellerDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYOUT_SELLER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutSeller extends ReplicaSeller {
    @Builder
    public PayoutSeller(
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
        super(id, createdAt, updatedAt, userId, sellerType, storeName, businessNum, representativeName, contactEmail, contactPhone, address1, address2, status);
    }

    public boolean isSystem() {
        return "system".equals(getStoreName());
    }

    public SellerDto toDto() {
        return SellerDto.builder()
                .id(getId())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .userId(getUserId())
                .sellerType(getSellerType())
                .storeName(getStoreName())
                .businessNum(getBusinessNum())
                .representativeName(getRepresentativeName())
                .contactEmail(getContactEmail())
                .contactPhone(getContactPhone())
                .address1(getAddress1())
                .address2(getAddress2())
                .status(getStatus())
                .build();
    }
}
