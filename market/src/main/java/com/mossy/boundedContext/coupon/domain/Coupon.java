package com.mossy.boundedContext.coupon.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "MARKET_COUPON")
@AttributeOverride(name = "id", column = @Column(name = "coupon_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Coupon extends BaseIdAndTime {

    @Column(name = "issuer_id", nullable = false)
    private Long issuerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "issuer_type", nullable = false)
    private IssuerType issuerType;

    @Column(name = "product_item_id", nullable = false)
    private Long productItemId;

    @Column(name = "coupon_name", nullable = false)
    private String couponName;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false)
    private CouponType couponType;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "is_active", nullable = false)
    private boolean isActive = false;

    // 정률 쿠폰은 maxDiscountAmount(최대 할인 한도)를 초과할 경우, 최대 할인 한도로 반환
    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        if (couponType == CouponType.FIXED) {
            return discountValue;
        }

        BigDecimal discount = originalPrice.multiply(discountValue)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

        if (maxDiscountAmount == null || discount.compareTo(maxDiscountAmount) <= 0) {
            return discount;
        }

        return maxDiscountAmount;
    }

    public static Coupon create(
            Long issuerId,
            IssuerType issuerType,
            Long productItemId,
            String couponName,
            CouponType couponType,
            BigDecimal discountValue,
            BigDecimal maxDiscountAmount,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        return Coupon.builder()
            .issuerId(issuerId)
            .issuerType(issuerType)
            .productItemId(productItemId)
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