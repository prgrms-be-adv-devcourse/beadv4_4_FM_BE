package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivateCouponUseCase {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(Coupon coupon) {
        coupon.activate();
    }
}
