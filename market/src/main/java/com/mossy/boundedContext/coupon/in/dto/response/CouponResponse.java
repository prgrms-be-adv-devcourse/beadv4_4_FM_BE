package com.mossy.boundedContext.coupon.in.dto.response;

import com.mossy.boundedContext.coupon.domain.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        Long couponId,
        String couponName,
        CouponType couponType,
        BigDecimal discountValue,
        BigDecimal maxDiscountAmount,
        LocalDateTime endAt
) { }
