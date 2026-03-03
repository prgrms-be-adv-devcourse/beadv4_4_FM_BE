package com.mossy.shared.member.domain.entity;

import com.mossy.global.jpa.entity.BaseEntity;
import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

    @Column(precision = 10, scale = 7)
    protected BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    protected BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    protected SellerStatus status = SellerStatus.ACTIVE;

    @Column(name = "profile_image_url", length = 500)
    protected String profileImageUrl;

    public BaseSeller(
            Long userId,
            SellerType sellerType,
            String storeName,
            String businessNum,
            BigDecimal latitude,
            BigDecimal longitude,
            SellerStatus status,
            String profileImageUrl
    ) {
        this.userId = userId;
        this.sellerType = sellerType;
        this.storeName = storeName;
        this.businessNum = businessNum;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.profileImageUrl = profileImageUrl;
    }

    public void suspend() {
        this.status = SellerStatus.SUSPENDED;
    }

    public void activate() {
        this.status = SellerStatus.ACTIVE;
    }
}

