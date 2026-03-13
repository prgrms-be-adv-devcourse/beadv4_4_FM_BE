package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
import com.mossy.boundedContext.coupon.in.dto.request.CouponUpdateRequest;
import com.mossy.boundedContext.coupon.in.dto.response.CouponResponse;
import com.mossy.boundedContext.coupon.in.dto.response.SellerCouponPageResponse;
import com.mossy.boundedContext.coupon.in.dto.response.UserCouponResponse;
import com.mossy.exception.ErrorCode;
import com.mossy.global.aop.PreventDuplicate;
import com.mossy.shared.market.enums.CouponType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final CreateSellerCouponUseCase createSellerCouponUseCase;
    private final CreateAdminCouponUseCase createAdminCouponUseCase;
    private final UpdateSellerCouponUseCase updateSellerCouponUseCase;
    private final DeactivateSellerCouponUseCase deactivateSellerCouponUseCase;
    private final DeleteSellerCouponUseCase deleteSellerCouponUseCase;
    private final GetDownloadableCouponsUseCase getDownloadableCouponsUseCase;
    private final DownloadCouponUseCase downloadCouponUseCase;
    private final GetMyUserCouponsUseCase getMyUserCouponsUseCase;
    private final GetApplicableCouponsUseCase getApplicableCouponsUseCase;
    private final UseCouponsUseCase useCouponsUseCase;
    private final RestoreCouponsUseCase restoreCouponsUseCase;
    private final GetSellerCouponsUseCase getSellerCouponsUseCase;
    private final GetUserCouponsUseCase getUserCouponsUseCase;
    private final GetActivatableCouponsUseCase getActivatableCouponsUseCase;
    private final ActivateCouponUseCase activateCouponUseCase;
    private final GetExpiredCouponsUseCase getExpiredCouponsUseCase;
    private final ExpireCouponUseCase expireCouponUseCase;

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

    public void deleteSellerCoupon(Long sellerId, Long couponId) {
        deleteSellerCouponUseCase.delete(sellerId, couponId);
    }

    public List<CouponResponse> getDownloadableCoupons(Long productItemId, Long userId) {
        return getDownloadableCouponsUseCase.get(productItemId, userId);
    }

    @PreventDuplicate(keyPrefix = "user-coupon:create:prevent", errorCode = ErrorCode.COUPON_DOWNLOAD_LOCKED)
    public Long downloadCoupon(Long couponId, Long userId) {
        return downloadCouponUseCase.download(couponId, userId);
    }

    public Page<UserCouponResponse> getMyUserCoupons(Long userId, UserCouponStatus status, CouponType couponType, Pageable pageable) {
        return getMyUserCouponsUseCase.get(userId, status, couponType, pageable);
    }

    public List<UserCouponResponse> getApplicableCoupons(Long userId, List<Long> productItemIds) {
        return getApplicableCouponsUseCase.get(userId, productItemIds);
    }

    public Map<Long, UserCoupon> getUserCoupons(List<Long> userCouponIds) {
        return getUserCouponsUseCase.getUserCoupons(userCouponIds);
    }

    public void useCoupons(List<Long> userCouponIds) {
        useCouponsUseCase.use(userCouponIds);
    }

    public void restoreCoupons(List<Long> userCouponIds) {
        restoreCouponsUseCase.restore(userCouponIds);
    }

    public SellerCouponPageResponse getSellerCoupons(
            Long sellerId,
            String status,
            CouponType couponType,
            Pageable pageable
    ) {
        return getSellerCouponsUseCase.getSellerCoupons(sellerId, status, couponType, pageable);
    }

    public void activateCoupons() {
        List<Coupon> activatableCoupons = getActivatableCouponsUseCase.execute();

        for (Coupon coupon : activatableCoupons) {
            try {
                activateCouponUseCase.execute(coupon);
            } catch (Exception e) {
                log.error("쿠폰 활성화 실패 - couponId: {}, error: {}",
                        coupon.getId(), e.getMessage(), e);
            }
        }
    }

    public void expireCoupons() {
        List<Coupon> expiredCoupons = getExpiredCouponsUseCase.execute();

        for (Coupon coupon : expiredCoupons) {
            try {
                expireCouponUseCase.execute(coupon);
            } catch (Exception e) {
                log.error("쿠폰 만료 실패 - couponId: {}, error: {}",
                        coupon.getId(), e.getMessage(), e);
            }
        }
    }
}
