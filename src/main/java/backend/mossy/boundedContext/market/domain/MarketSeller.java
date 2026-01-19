package backend.mossy.boundedContext.market.domain;

import backend.mossy.shared.member.domain.seller.ReplicaSeller;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "MARKET_SELLER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AttributeOverride(name = "id", column = @Column(name = "seller_id"))
public class MarketSeller extends ReplicaSeller {
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
                sellerStatus);
    }
}