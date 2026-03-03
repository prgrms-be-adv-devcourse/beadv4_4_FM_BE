package com.mossy.boundedContext.coupon.in.dto.response;

import com.mossy.shared.market.enums.CouponType;
import com.mossy.boundedContext.coupon.domain.UserCouponStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserCouponResponse(
        Long userCouponId,
        String couponName,
        CouponType couponType,
        BigDecimal discountValue,
        BigDecimal maxDiscountAmount,
        UserCouponStatus status,
        LocalDateTime endAt
) {}
