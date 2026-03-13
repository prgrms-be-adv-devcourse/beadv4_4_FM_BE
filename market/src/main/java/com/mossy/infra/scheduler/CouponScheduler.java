package com.mossy.infra.scheduler;

import com.mossy.boundedContext.coupon.app.CouponFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponFacade couponFacade;

    @Scheduled(cron = "0 * * * * *")
    public void activateCoupons() {
        couponFacade.activateCoupons();
    }

    @Scheduled(cron = "30 * * * * *")
    public void expireCoupons() {
        couponFacade.expireCoupons();
    }
}
