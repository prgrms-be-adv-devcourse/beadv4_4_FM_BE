package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
import com.mossy.boundedContext.coupon.in.dto.request.CouponUpdateRequest;
import com.mossy.boundedContext.coupon.in.dto.response.CouponResponse;
import com.mossy.boundedContext.coupon.in.dto.response.UserCouponResponse;
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
    private final UpdateSellerCouponUseCase updateSellerCouponUseCase;
    private final DeactivateSellerCouponUseCase deactivateSellerCouponUseCase;
    private final GetDownloadableCouponsUseCase getDownloadableCouponsUseCase;
    private final DownloadCouponUseCase downloadCouponUseCase;
    private final GetMyUserCouponsUseCase getMyUserCouponsUseCase;
    private final GetApplicableCouponsUseCase getApplicableCouponsUseCase;
    private final CalculateCouponDiscountsUseCase calculateCouponDiscountsUseCase;
    private final UseCouponsUseCase useCouponsUseCase;
    private final RestoreCouponsUseCase restoreCouponsUseCase;

    public Long createSellerCoupon(Long sellerId, CouponCreateRequest request) {
        return createSellerCouponUseCase.create(sellerId, request);
    }

    public Long createAdminCoupon(Long adminId, CouponCreateRequest request) {
        return createAdminCouponUseCase.create(adminId, request);
    }

    public void updateSellerCoupon(Long sellerId, Long couponId, CouponUpdateRequest request) {
        updateSellerCouponUseCase.update(sellerId, couponId, request);
    }

    public void deactivateSellerCoupon(Long sellerId, Long couponId) {
        deactivateSellerCouponUseCase.deactivate(sellerId, couponId);
    }

    public List<CouponResponse> getDownloadableCoupons(Long productItemId, Long userId) {
        return getDownloadableCouponsUseCase.get(productItemId, userId);
    }

    public void downloadCoupon(Long couponId, Long userId) {
        downloadCouponUseCase.download(couponId, userId);
    }

    public List<UserCouponResponse> getMyUserCoupons(Long userId) {
        return getMyUserCouponsUseCase.get(userId);
    }

    public List<UserCouponResponse> getApplicableCoupons(Long productItemId, Long userId) {
        return getApplicableCouponsUseCase.get(productItemId, userId);
    }

    public Map<Long, BigDecimal> calculateDiscounts(Map<Long, BigDecimal> userCouponPriceMap) {
        return calculateCouponDiscountsUseCase.calculateDiscounts(userCouponPriceMap);
    }

    public void useCoupons(List<Long> userCouponIds) {
        useCouponsUseCase.use(userCouponIds);
    }

    public void restoreCoupons(List<Long> userCouponIds) {
        restoreCouponsUseCase.restore(userCouponIds);
    }
}
