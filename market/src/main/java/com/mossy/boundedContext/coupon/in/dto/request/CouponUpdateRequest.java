package com.mossy.boundedContext.coupon.in.dto.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponUpdateRequest(
        String couponName,

        @Positive(message = "할인 값은 0보다 커야 합니다")
        BigDecimal discountValue,

        @Positive(message = "최대 할인 금액은 0보다 커야 합니다")
        BigDecimal maxDiscountAmount,

        LocalDateTime endAt
) {}
