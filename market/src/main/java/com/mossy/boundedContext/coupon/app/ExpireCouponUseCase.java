package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpireCouponUseCase {

    private final UserCouponRepository userCouponRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(Coupon coupon) {
        coupon.expire();

        userCouponRepository.findAllByCouponIdInAndStatus(
            List.of(coupon.getId()),
            UserCouponStatus.UNUSED
        ).forEach(UserCoupon::expire);
    }
}
