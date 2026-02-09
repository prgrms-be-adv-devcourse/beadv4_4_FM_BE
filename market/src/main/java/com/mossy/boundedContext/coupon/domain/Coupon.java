package com.mossy.boundedContext.coupon.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MARKET_COUPON")
@AttributeOverride(name = "id", column = @Column(name = "coupon_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Coupon extends BaseIdAndTime {

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "coupon_name", nullable = false)
    private String couponName;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false)
    private CouponType couponType;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "max_discount_amount", nullable = false)
    private BigDecimal maxDiscountAmount;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "is_active", nullable = false)
    private boolean isActive = false;

    public static Coupon create(
            Long sellerId,
            Long productId,
            String couponName,
            CouponType couponType,
            BigDecimal discountValue,
            BigDecimal maxDiscountAmount,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        return Coupon.builder()
            .sellerId(sellerId)
            .productId(productId)
            .couponName(couponName)
            .couponType(couponType)
            .discountValue(discountValue)
            .maxDiscountAmount(maxDiscountAmount)
            .startAt(startAt)
            .endAt(endAt)
            .isActive(false)
            .build();
}
}