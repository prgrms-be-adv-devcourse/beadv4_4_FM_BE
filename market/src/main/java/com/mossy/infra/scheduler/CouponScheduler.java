package com.mossy.infra.scheduler;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void activateCoupons() {
        couponRepository.findActivatableCoupons(LocalDateTime.now())
                .forEach(Coupon::activate);
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void deactivateCoupons() {
        List<Coupon> expiredCoupons = couponRepository.findExpiredCoupons(LocalDateTime.now());
        if (expiredCoupons.isEmpty()) return;

        expiredCoupons.forEach(Coupon::deactivate);

        List<Long> couponIds = expiredCoupons.stream().map(Coupon::getId).toList();

        userCouponRepository.findAllByCouponIdInAndStatus(couponIds, UserCouponStatus.UNUSED)
                .forEach(UserCoupon::expire);
    }
}
