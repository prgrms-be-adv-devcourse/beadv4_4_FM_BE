package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.in.dto.response.CouponResponse;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDownloadableCouponsUseCase {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public List<CouponResponse> get(Long productItemId, Long userId) {
        return couponRepository.findDownloadableCoupons(productItemId, userId);
    }
}
