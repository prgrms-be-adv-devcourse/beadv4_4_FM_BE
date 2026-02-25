package com.mossy.infra.scheduler;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void activateCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> coupons = couponRepository.findActivatableCoupons(now);

        log.info("[activateCoupons] 현재: {}, 대상: {}개", now, coupons.size());
        coupons.forEach(c -> {
            log.info("[activateCoupons] 활성화 전 - ID:{}, 종료:{}, deact:{}, active:{}",
                c.getId(), c.getEndAt(), c.isDeactivated(), c.isActive());
            c.activate();
            log.info("[activateCoupons] 활성화 후 - ID:{}, active:{}", c.getId(), c.isActive());
        });
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void expireCoupons() {
        List<Coupon> expiredCoupons = couponRepository.findExpiredCoupons(LocalDateTime.now());
        if (expiredCoupons.isEmpty()) return;

        expiredCoupons.forEach(Coupon::expire);

        List<Long> couponIds = expiredCoupons.stream().map(Coupon::getId).toList();

        userCouponRepository.findAllByCouponIdInAndStatus(couponIds, UserCouponStatus.UNUSED)
                .forEach(UserCoupon::expire);
    }
}
