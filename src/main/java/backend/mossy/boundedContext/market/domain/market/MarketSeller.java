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
            BigDecimal latitude,
            BigDecimal longitude,
            SellerStatus sellerStatus) {
        super(
                id,
                createdAt,
                updatedAt,
                userId,
                sellerType,
                storeName,
                businessNum,
                representativeName,
                contactEmail,
                contactPhone,
                address1,
                address2,
                latitude,
                longitude,
                sellerStatus);
    }

    public static MarketSeller from(SellerDto seller) {
        return MarketSeller.builder()
                .id(seller.id())
                .userId(seller.userId())
                .sellerType(seller.sellerType())
                .storeName(seller.storeName())
                .businessNum(seller.businessNum())
                .representativeName(seller.representativeName())
                .contactEmail(seller.contactEmail())
                .contactPhone(seller.contactPhone())
                .address1(seller.address1())
                .address2(seller.address2())
                .latitude(seller.latitude())
                .longitude(seller.longitude())
                .sellerStatus(seller.status())
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
                .representativeName(getRepresentativeName())
                .contactEmail(getContactEmail())
                .contactPhone(getContactPhone())
                .address1(getAddress1())
                .address2(getAddress2())
                .latitude(getLatitude())
                .longitude(getLongitude())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}