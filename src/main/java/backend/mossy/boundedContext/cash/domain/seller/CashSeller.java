package backend.mossy.boundedContext.cash.domain.seller;

import backend.mossy.shared.cash.dto.common.CashSellerDto;
import backend.mossy.shared.member.domain.seller.ReplicaSeller;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CASH_SELLER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "seller_id"))
public class CashSeller extends ReplicaSeller {

    @Builder
    public CashSeller(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
        Long userId, SellerType sellerType, String storeName, String businessNum,
        String representativeName, String contactEmail, String contactPhone, String address1,
        String address2, SellerStatus status) {
        super(id, createdAt, updatedAt, userId, sellerType, storeName, businessNum,
            representativeName,
            contactEmail, contactPhone, address1, address2, status);
    }

    // Payout이나 Cash 서비스에서 사용할 DTO 변환 로직
    public CashSellerDto toDto() {
        return CashSellerDto.builder()
            .id(getId())
            .storeName(getStoreName())
            .representativeName(getRepresentativeName())
            .build();
    }
}