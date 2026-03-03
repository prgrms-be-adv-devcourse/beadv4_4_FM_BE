package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.in.dto.response.UserCouponResponse;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import com.mossy.shared.market.enums.CouponType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyUserCouponsUseCase {

    private final UserCouponRepository userCouponRepository;

    @Transactional(readOnly = true)
    public Page<UserCouponResponse> get(Long userId, UserCouponStatus status, CouponType couponType, Pageable pageable) {
        return userCouponRepository.findMyUserCoupons(userId, status, couponType, pageable);
    }
}
