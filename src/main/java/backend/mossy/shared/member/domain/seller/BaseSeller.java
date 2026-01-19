package backend.mossy.shared.member.domain.seller;

import backend.mossy.global.jpa.entity.BaseEntity;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import backend.mossy.shared.member.domain.user.SourceUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseSeller extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    protected Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_type", nullable = false, length = 20)
    protected SellerType sellerType;

    @Column(name = "store_name", nullable = false, unique = true, length = 255)
    protected String storeName;

    @Column(name = "business_num", unique = true, length = 20)
    protected String businessNum;

    @Column(name = "representative_name", nullable = false, length = 100)
    protected String representativeName;

    @Column(name = "contact_email", length = 255)
    protected String contactEmail;

    @Column(name = "contact_phone", length = 20)
    protected String contactPhone;

    @Column(name = "address1", nullable = false, length = 200)
    protected String address1;

    @Column(name = "address2", length = 200)
    protected String address2;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    protected SellerStatus status = SellerStatus.PENDING;

    public BaseSeller(Long userId, SellerType sellerType, String storeName, String businessNum, String representativeName, String contactEmail, String contactPhone, String address1, String address2, SellerStatus status) {
        this.userId = userId;
        this.sellerType = sellerType;
        this.storeName = storeName;
        this.businessNum = businessNum;
        this.representativeName = representativeName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.address1 = address1;
        this.address2 = address2;
        this.status = status;
    }
}

