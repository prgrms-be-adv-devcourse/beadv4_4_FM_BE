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
import java.math.BigDecimal;
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
    public CashSeller(
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
                id,
                userId,
                sellerType,
                storeName,
                businessNum,
                latitude,
                longitude,
                status,
                createdAt,
                updatedAt
        );
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
            .latitude(seller.latitude())
            .longitude(seller.longitude())
            .status(seller.status())
            .build();
    }

    public CashSellerDto toDto() {
        return CashSellerDto.builder()
            .id(getId())
            .createdAt(getCreatedAt())
            .updatedAt(getUpdatedAt())
            .userId(getUserId())
            .sellerType(getSellerType())
            .storeName(getStoreName())
            .businessNum(getBusinessNum())
            .latitude(getLatitude())
            .longitude(getLongitude())
            .status(getStatus())
            .build();
    }
}