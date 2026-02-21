package com.mossy.boundedContext.coupon.dto;

import com.mossy.shared.market.enums.CouponType;

import java.math.BigDecimal;

public record CouponDiscountInfo(
        BigDecimal discountAmount,
        CouponType couponType
) {}
