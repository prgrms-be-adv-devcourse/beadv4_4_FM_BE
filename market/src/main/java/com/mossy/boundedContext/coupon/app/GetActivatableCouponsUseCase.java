package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetActivatableCouponsUseCase {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public List<Coupon> execute() {
        return couponRepository.findActivatableCoupons(LocalDateTime.now());
    }
}
