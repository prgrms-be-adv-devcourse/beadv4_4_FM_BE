package backend.mossy.boundedContext.member.domain;

import backend.mossy.shared.member.domain.seller.SellerRequest;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.member.domain.seller.SourceSeller;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "SELLER",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_sellers_user_id",
                        columnNames = {"user_id"}),
                @UniqueConstraint(
                        name = "uk_sellers_business_num",
                        columnNames = {"business_num"})
        }
)
public class Seller extends SourceSeller {

        private Seller(Long userId, SellerType sellerType, String storeName,
                       String businessNum, BigDecimal latitude, BigDecimal longitude) {
                super(
                        userId,
                        sellerType,
                        storeName,
                        businessNum,
                        latitude,
                        longitude,
                        SellerStatus.ACTIVE
                );
        }

        public static Seller fromRequest(SellerRequest req) {
                return new Seller(
                        req.getUser().getId(),
                        req.getSellerType(),
                        req.getStoreName(),
                        req.getBusinessNum(),
                        req.getLatitude(),
                        req.getLongitude()
                );
        }

}
