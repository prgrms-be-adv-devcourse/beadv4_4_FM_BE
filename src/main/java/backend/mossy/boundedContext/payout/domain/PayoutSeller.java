package backend.mossy.boundedContext.payout.domain;

import backend.mossy.shared.member.domain.seller.BaseSeller;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.payout.dto.response.PayoutSellerDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class PayoutSeller extends BaseSeller {
    @Id
    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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
        super(userId, sellerType, storeName, businessNum, representativeName, contactEmail, contactPhone, address1, address2);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        setStatus(status); // Use protected setter from BaseSeller
    }

    @Override
    public Long getId() {
        return id;
    }

    public PayoutSellerDto toDto() {
        return PayoutSellerDto.builder()
                .id(this.getId())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .userId(this.getUserId())
                .sellerType(this.getSellerType())
                .storeName(this.getStoreName())
                .businessNum(this.getBusinessNum())
                .representativeName(this.getRepresentativeName())
                .contactEmail(this.getContactEmail())
                .contactPhone(this.getContactPhone())
                .address1(this.getAddress1())
                .address2(this.getAddress2())
                .status(this.getStatus())
                .build();
    }

    public boolean isSystem() {
        return "system".equals(getStoreName());
    }
}
