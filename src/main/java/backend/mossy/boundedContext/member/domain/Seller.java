package backend.mossy.boundedContext.member.domain;

import backend.mossy.shared.member.domain.seller.SellerRequest;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.member.domain.seller.SourceSeller;
import jakarta.persistence.Column;
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

        @Column(name = "representative_name", nullable = false, length = 100)
        private String representativeName;

        @Column(name = "contact_email", length = 255)
        private String contactEmail;

        @Column(name = "contact_phone", length = 20)
        private String contactPhone;

        @Column(name = "address1", nullable = false, length = 200)
        private String address1;

        @Column(name = "address2", length = 200)
        private String address2;

        private Seller(Long userId,
                       SellerType sellerType,
                       String storeName,
                       String businessNum,
                       BigDecimal latitude,
                       BigDecimal longitude,
                       String representativeName,
                       String contactEmail,
                       String contactPhone,
                       String address1,
                       String address2) {
                super(
                        userId,
                        sellerType,
                        storeName,
                        businessNum,
                        latitude,
                        longitude,
                        SellerStatus.ACTIVE
                );

                this.representativeName = representativeName;
                this.contactEmail = contactEmail;
                this.contactPhone = contactPhone;
                this.address1 = address1;
                this.address2 = address2;
        }

        public static Seller createFromRequest(SellerRequest req) {
                return new Seller(
                        req.getUser().getId(),
                        req.getSellerType(),
                        req.getStoreName(),
                        req.getBusinessNum(),
                        req.getLatitude(),
                        req.getLongitude(),
                        req.getRepresentativeName(),
                        req.getContactEmail(),
                        req.getContactPhone(),
                        req.getAddress1(),
                        req.getAddress2()
                );
        }

}
