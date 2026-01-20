package backend.mossy.boundedContext.cash.domain.seller;

import backend.mossy.boundedContext.cash.domain.user.CashUser;
import backend.mossy.shared.cash.dto.event.CashSellerDto;
import backend.mossy.shared.member.domain.seller.ReplicaSeller;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.member.dto.event.SellerDto;
import backend.mossy.shared.member.dto.event.UserDto;
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

    public static CashSeller from(SellerDto seller) {
        return CashSeller.builder()
            .id(seller.id())
            .createdAt(seller.createdAt())
            .updatedAt(seller.updatedAt())
            .userId(seller.userId())
            .sellerType(seller.sellerType())
            .storeName(seller.storeName())
            .businessNum(seller.businessNum())
            .representativeName(seller.representativeName())
            .contactEmail(seller.contactEmail())
            .contactPhone(seller.contactPhone())
            .address1(seller.address1())
            .address2(seller.address2())
            .status(seller.status())
            .build();
    }

    // Payout이나 Cash 서비스에서 사용할 DTO 변환 로직
    public CashSellerDto toDto() {
        return CashSellerDto.builder()
            .id(getId()) //
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