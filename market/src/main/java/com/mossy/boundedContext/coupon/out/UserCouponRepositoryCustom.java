package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.in.dto.response.UserCouponResponse;

import java.util.List;

public interface UserCouponRepositoryCustom {

    List<UserCouponResponse> findMyUserCoupons(Long userId);

    List<UserCouponResponse> findApplicableCoupons(Long productItemId, Long userId);
}
