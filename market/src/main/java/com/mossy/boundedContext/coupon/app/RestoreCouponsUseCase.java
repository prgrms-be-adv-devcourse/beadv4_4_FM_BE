package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestoreCouponsUseCase {

    private final UserCouponRepository userCouponRepository;

    @Transactional
    public void restore(List<Long> userCouponIds) {
        if (userCouponIds.isEmpty()) {
            return;
        }

        userCouponRepository.findAllById(userCouponIds).forEach(UserCoupon::restore);
    }
}
