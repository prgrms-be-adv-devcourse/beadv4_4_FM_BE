package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final CreateSellerCouponUseCase createSellerCouponUseCase;
    private final CreateAdminCouponUseCase createAdminCouponUseCase;
    private final CalculateCouponDiscountsUseCase calculateCouponDiscountsUseCase;
    private final UseCouponsUseCase useCouponsUseCase;

    public Long createSellerCoupon(Long sellerId, CouponCreateRequest request) {
        return createSellerCouponUseCase.create(sellerId, request);
    }

    public Long createAdminCoupon(Long adminId, CouponCreateRequest request) {
        return createAdminCouponUseCase.create(adminId, request);
    }

    public Map<Long, BigDecimal> calculateDiscounts(Map<Long, BigDecimal> userCouponPriceMap) {
        return calculateCouponDiscountsUseCase.calculateDiscounts(userCouponPriceMap);
    }

    public void useCoupons(List<Long> userCouponIds) {
        useCouponsUseCase.use(userCouponIds);
    }
}
