package backend.mossy.shared.member.domain.seller;

import backend.mossy.global.jpa.entity.BaseTimeOnly;
import backend.mossy.shared.member.domain.SourceUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sellers")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seller extends BaseTimeOnly {


    //seller_id == user_id (공유PK)
    @Id
    @Column(name = "seller_id")
    private Long seller_id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private SourceUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_type", nullable = false, length = 20)
    private SellerType sellerType;

    @Column(name = "store_name", nullable = false, unique = true, length = 255)
    private String storeName;

    @Column(name = "business_num", unique = true, length = 20)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SellerStatus status = SellerStatus.PENDING;

    public static Seller register(
            SourceUser user,
            SellerType sellerType,
            String storeName,
            String businessNum,
            String representativeName,
            String contactEmail,
            String contactPhone,
            String address1,
            String address2
    ) {
        Seller s = new Seller();
        s.user = user;
        s.seller_id = user.getId();
        s.sellerType = sellerType;
        s.storeName = storeName;
        s.businessNum = businessNum;
        s.representativeName = representativeName;
        s.contactEmail = contactEmail;
        s.contactPhone = contactPhone;
        s.address1 = address1;
        s.address2 = address2;
        s.status = SellerStatus.PENDING;
        return s;
    }

    public boolean isApproved() {
        return this.status == SellerStatus.APPROVED;
    }
}