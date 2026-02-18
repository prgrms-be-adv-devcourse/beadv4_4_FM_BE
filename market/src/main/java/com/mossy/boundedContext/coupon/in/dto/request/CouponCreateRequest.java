package com.mossy.boundedContext.coupon.in.dto.request;

import com.mossy.boundedContext.coupon.domain.CouponType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponCreateRequest(
        @NotNull(message = "상품 ID는 필수입니다")
        Long productItemId,

        @NotBlank(message = "쿠폰 이름은 필수입니다")
        String couponName,

        @NotNull(message = "쿠폰 타입은 필수입니다")
        CouponType couponType,

        @NotNull(message = "할인 값은 필수입니다")
        @Positive(message = "할인 값은 0보다 커야 합니다")
        BigDecimal discountValue,

        @Positive(message = "최대 할인 금액은 0보다 커야 합니다")
        BigDecimal maxDiscountAmount,

        @NotNull(message = "시작 일시는 필수입니다")
        LocalDateTime startAt,

        @NotNull(message = "종료 일시는 필수입니다")
        LocalDateTime endAt
) {
}
