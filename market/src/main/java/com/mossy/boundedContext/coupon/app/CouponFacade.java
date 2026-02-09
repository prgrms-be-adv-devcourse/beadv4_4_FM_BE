package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponFacade {
    private final CreateCouponUseCase createCouponUseCase;

    public Long createCoupon(Long sellerId, CouponCreateRequest request) {
        return createCouponUseCase.createCoupon(sellerId, request);
    }
}
