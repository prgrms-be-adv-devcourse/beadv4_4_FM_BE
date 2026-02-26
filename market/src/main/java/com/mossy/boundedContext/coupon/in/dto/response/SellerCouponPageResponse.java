package com.mossy.boundedContext.coupon.in.dto.response;

import com.mossy.boundedContext.coupon.out.dto.CouponStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public record SellerCouponPageResponse(
        CouponSummary summary,
        Page<SellerCouponListResponse> coupons
) {
    public record CouponSummary(
            Long totalCount,
            Long activeCount,
            Long inactiveCount,
            Long expiredCount
    ) {}

    public static SellerCouponPageResponse of(
            List<SellerCouponListResponse> content,
            Pageable pageable,
            CouponStatistics stats
    ) {
        Page<SellerCouponListResponse> page = new PageImpl<>(content, pageable, stats.totalCount());
        CouponSummary summary = new CouponSummary(
                stats.totalCount(),
                stats.activeCount(),
                stats.inactiveCount(),
                stats.expiredCount()
        );
        return new SellerCouponPageResponse(summary, page);
    }
}
