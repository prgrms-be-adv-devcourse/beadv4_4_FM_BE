package com.mossy.boundedContext.order.in.dto.response;

import com.mossy.shared.market.enums.CouponType;
import com.mossy.shared.market.enums.OrderState;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderDetailResponse(
                Long orderItemId,
                Long productItemId,
                int quantity,
                BigDecimal originalPrice,
                BigDecimal discountAmount,
                BigDecimal finalPrice,
                String couponName,
                CouponType couponType,
                String sellerName,
                OrderState status) {
}