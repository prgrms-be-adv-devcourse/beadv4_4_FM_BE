package com.mossy.boundedContext.coupon.domain;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
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

    // 수동 비활성화 여부
    // 이 컬럼이 없으면 스케쥴러에서 계속 isActive를 true로 만듦
    @Convert(converter = YesNoConverter.class)
    @Column(name = "deactivated", nullable = false)
    private boolean deactivated = false;

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
        validateStartAtFuture(startAt);
        validatePeriod(startAt, endAt);
        return Coupon.builder()
            .issuerId(issuerId)
            .issuerType(issuerType)
            .productItemId(productItemId)
            .couponName(couponName)
            .couponType(couponType)
            .discountValue(discountValue)
            .maxDiscountAmount(couponType == CouponType.FIXED ? null : maxDiscountAmount)
            .startAt(startAt)
            .endAt(endAt)
            .isActive(false)
            .build();
    }

    public void activate() {
        if (this.deactivated) return;
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
        this.deactivated = true;
    }

    public void update(
            String couponName,
            BigDecimal discountValue,
            BigDecimal maxDiscountAmount,
            LocalDateTime endAt
    ) {
        LocalDateTime newEndAt = endAt != null ? endAt : this.endAt;
        validatePeriod(this.startAt, newEndAt);

        if (couponName != null) this.couponName = couponName;
        if (discountValue != null) this.discountValue = discountValue;

        if (this.couponType == CouponType.PERCENTAGE && maxDiscountAmount != null)
            this.maxDiscountAmount = maxDiscountAmount;

        this.endAt = newEndAt;
    }

    // 정률 쿠폰은 maxDiscountAmount(최대 할인 한도)를 초과할 경우, 최대 할인 한도로 반환
    // 정액 쿠폰은 maxDiscountAmount가 존재하지 않는다.
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

    // 쿠폰을 등록한 판매자 본인인지 검증
    public void validateOwnerSeller(Long sellerId) {
        if (!this.issuerId.equals(sellerId) || this.issuerType != IssuerType.SELLER) {
            throw new DomainException(ErrorCode.COUPON_ACCESS_DENIED);
        }
    }

    // 쿠폰 시작 시간보다 끝나는 시간이 미래일 때 검증
    private static void validatePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (!startAt.isBefore(endAt)) {
            throw new DomainException(ErrorCode.INVALID_COUPON_PERIOD);
        }
    }

    // 현재 시간이 쿠폰 시작 시간보다 과거일 때 검증
    private static void validateStartAtFuture(LocalDateTime startAt) {
        if (!LocalDateTime.now().isBefore(startAt)) {
            throw new DomainException(ErrorCode.INVALID_COUPON_START_AT);
        }
    }
}
