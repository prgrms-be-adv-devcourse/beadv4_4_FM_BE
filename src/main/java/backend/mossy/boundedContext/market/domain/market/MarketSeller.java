package backend.mossy.boundedContext.market.domain.market;

import backend.mossy.shared.market.dto.event.MarketSellerDto;
import backend.mossy.shared.member.domain.seller.ReplicaSeller;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.member.dto.event.SellerDto;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MARKET_SELLER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "seller_id"))
public class MarketSeller extends ReplicaSeller {
    @Builder
    public MarketSeller(
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

    public static MarketSeller from(SellerDto seller) {
        return MarketSeller.builder()
                .id(seller.id())
                .userId(seller.userId())
                .sellerType(seller.sellerType())
                .storeName(seller.storeName())
                .businessNum(seller.businessNum())
                .latitude(seller.latitude())
                .longitude(seller.longitude())
                .status(seller.status())
                .createdAt(seller.createdAt())
                .updatedAt(seller.updatedAt())
                .build();
    }

    public MarketSellerDto toDto() {
        return MarketSellerDto.builder()
                .id(getId())
                .userId(getUserId())
                .sellerType(getSellerType())
                .storeName(getStoreName())
                .businessNum(getBusinessNum())
                .latitude(getLatitude())
                .longitude(getLongitude())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}