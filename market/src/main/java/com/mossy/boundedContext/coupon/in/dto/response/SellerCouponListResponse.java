package com.mossy.boundedContext.coupon.in.dto.response;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.shared.market.enums.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SellerCouponListResponse(
        Long couponId,
        Long productItemId,
        String couponName,
        CouponType couponType,
        BigDecimal discountValue,
        BigDecimal maxDiscountAmount,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean isActive,
        LocalDateTime createdAt
) {
    public static SellerCouponListResponse from(Coupon coupon) {
        return new SellerCouponListResponse(
                coupon.getId(),
                coupon.getProductItemId(),
                coupon.getCouponName(),
                coupon.getCouponType(),
                coupon.getDiscountValue(),
                coupon.getMaxDiscountAmount(),
                coupon.getStartAt(),
                coupon.getEndAt(),
                coupon.isActive(),
                coupon.getCreatedAt()
        );
    }
}
