package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.dto.CouponDiscountInfo;
import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
import com.mossy.boundedContext.coupon.in.dto.request.CouponUpdateRequest;
import com.mossy.boundedContext.coupon.in.dto.response.CouponResponse;
import com.mossy.boundedContext.coupon.in.dto.response.SellerCouponListResponse;
import com.mossy.boundedContext.coupon.in.dto.response.UserCouponResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final RedissonClient redissonClient;
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
    private final GetSellerCouponsUseCase getSellerCouponsUseCase;

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

    public Long downloadCoupon(Long couponId, Long userId) {
        String lockKey = "coupon:download:" + userId + ":" + couponId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(2, 5, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new DomainException(ErrorCode.COUPON_DOWNLOAD_LOCKED);
            }

            return downloadCouponUseCase.download(couponId, userId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DomainException(ErrorCode.COUPON_DOWNLOAD_FAILED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public List<UserCouponResponse> getMyUserCoupons(Long userId) {
        return getMyUserCouponsUseCase.get(userId);
    }

    public List<UserCouponResponse> getApplicableCoupons(Long userId, List<Long> productItemIds) {
        return getApplicableCouponsUseCase.get(userId, productItemIds);
    }

    public Map<Long, CouponDiscountInfo> calculateDiscounts(Map<Long, BigDecimal> userCouponPriceMap) {
        return calculateCouponDiscountsUseCase.calculateDiscounts(userCouponPriceMap);
    }

    public void useCoupons(List<Long> userCouponIds) {
        useCouponsUseCase.use(userCouponIds);
    }

    public void restoreCoupons(List<Long> userCouponIds) {
        restoreCouponsUseCase.restore(userCouponIds);
    }

    public List<SellerCouponListResponse> getSellerCoupons(Long sellerId) {
        return getSellerCouponsUseCase.getSellerCoupons(sellerId);
    }
}
