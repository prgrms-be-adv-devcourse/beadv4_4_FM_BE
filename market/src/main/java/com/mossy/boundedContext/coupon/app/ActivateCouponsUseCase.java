package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivateCouponsUseCase {

    private final CouponRepository couponRepository;

    @Transactional
    public void execute() {
        couponRepository.findActivatableCoupons(LocalDateTime.now())
                .forEach(Coupon::activate);
    }
}
