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
        String status,
        LocalDateTime createdAt) {
    public static SellerCouponListResponse from(Coupon coupon) {
        String status = determineStatus(coupon);
        return new SellerCouponListResponse(
                coupon.getId(),
                coupon.getProductItemId(),
                coupon.getCouponName(),
                coupon.getCouponType(),
                coupon.getDiscountValue(),
                coupon.getMaxDiscountAmount(),
                coupon.getStartAt(),
                coupon.getEndAt(),
                status,
                coupon.getCreatedAt());
    }

    private static String determineStatus(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now();

        // 비활성화 (판매자가 수동으로 중지)
        if (coupon.isDeactivated()) {
            return "INACTIVE";
        }

        // 종료됨 (기간 만료)
        if (now.isAfter(coupon.getEndAt())) {
            return "EXPIRED";
        }

        // 활성
        return "ACTIVE";
    }
}
