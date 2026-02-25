package com.mossy.boundedContext.coupon.out.dto;

public record CouponStatistics(
        Long totalCount,
        Long activeCount,
        Long inactiveCount,
        Long expiredCount
) {
}
