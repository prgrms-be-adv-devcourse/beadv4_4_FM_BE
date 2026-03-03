package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.in.dto.response.UserCouponResponse;
import com.mossy.shared.market.enums.CouponType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserCouponRepositoryCustom {

    Page<UserCouponResponse> findMyUserCoupons(Long userId, UserCouponStatus status, CouponType couponType, Pageable pageable);

    List<UserCouponResponse> findApplicableCoupons(Long userId, List<Long> productItemIds);
}
