package com.mossy.boundedContext.domain.seller;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import com.mossy.shared.member.domain.enums.SellerType;
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

    @Column(name = "user_id", nullable = false)
    private Long userId;

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
            BigDecimal longitude
    ) {
        SellerRequest r = new SellerRequest();
        r.activeUserId = userId;

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
            //TODO: Errorcode로 바꿀예정
            throw new IllegalStateException("판매자 요청이 '대기 중'상태가 아닙니다.");
        }
    }
}
