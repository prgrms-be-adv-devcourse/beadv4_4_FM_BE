package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpireCouponsUseCase {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @Transactional
    public void execute() {
        List<Coupon> expiredCoupons = couponRepository.findExpiredCoupons(LocalDateTime.now());

        expiredCoupons.forEach(Coupon::expire);

        List<Long> couponIds = expiredCoupons.stream()
                .map(Coupon::getId)
                .toList();

        userCouponRepository.findAllByCouponIdInAndStatus(couponIds, UserCouponStatus.UNUSED)
                .forEach(com.mossy.boundedContext.coupon.domain.UserCoupon::expire);
    }
}
