package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.in.dto.response.CouponResponse;

import java.util.List;

public interface CouponRepositoryCustom {

    List<CouponResponse> findDownloadableCoupons(Long productItemId, Long userId);
}
