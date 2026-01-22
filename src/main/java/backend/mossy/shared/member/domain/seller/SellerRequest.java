package backend.mossy.shared.member.domain.seller;

import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "SELLER_REQUEST",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_seller_request_active_user", columnNames = "active_user_id"),
                @UniqueConstraint(name = "uq_seller_request_business_num", columnNames = "business_num")
        }
)
public class SellerRequest extends BaseIdAndTime {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "active_user_id")
    private Long activeUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_type", nullable = false, length = 20)
    private SellerType sellerType;

    @Column(name = "store_name", nullable = false, length = 255)
    private String storeName;

    @Column(name = "business_num", nullable = false, length = 20)
    private String businessNum;

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

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SellerRequestStatus status;

    public static SellerRequest pending(
            User user,
            SellerType sellerType,
            String storeName,
            String businessNum,
            String representativeName,
            String contactEmail,
            String contactPhone,
            String address1,
            String address2,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        SellerRequest r = new SellerRequest();
        r.user = user;
        r.activeUserId = user.getId();

        r.sellerType = sellerType;
        r.storeName = storeName;
        r.businessNum = businessNum;

        r.representativeName = representativeName;
        r.contactEmail = contactEmail;
        r.contactPhone = contactPhone;

        r.address1 = address1;
        r.address2 = address2;
        r.latitude = latitude;
        r.longitude = longitude;

        r.status = SellerRequestStatus.PENDING;
        return r;
    }

    public void approve() {
        validatePending();
        this.status = SellerRequestStatus.APPROVED;
        this.activeUserId = null;
    }

    public void reject() {
        validatePending();
        this.status = SellerRequestStatus.REJECTED;
        this.activeUserId = null;
    }

    public void cancel() {
        validatePending();
        this.status = SellerRequestStatus.CANCELED;
        this.activeUserId = null;
    }

    private void validatePending() {
        if (this.status != SellerRequestStatus.PENDING) {
            throw new IllegalStateException("판매자 요청이 '대기 중'상태가 아닙니다.");
        }
    }
}
