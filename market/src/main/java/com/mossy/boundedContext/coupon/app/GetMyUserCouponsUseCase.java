package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.in.dto.response.UserCouponResponse;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyUserCouponsUseCase {

    private final UserCouponRepository userCouponRepository;

    @Transactional(readOnly = true)
    public List<UserCouponResponse> get(Long userId) {
        return userCouponRepository.findMyUserCoupons(userId);
    }
}
