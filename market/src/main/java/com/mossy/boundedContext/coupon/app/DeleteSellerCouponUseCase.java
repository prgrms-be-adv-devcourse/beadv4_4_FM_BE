package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteSellerCouponUseCase {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @Transactional
    public void delete(Long sellerId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new DomainException(ErrorCode.COUPON_NOT_FOUND));

        if (!coupon.canDelete(sellerId)) {
            throw new DomainException(ErrorCode.COUPON_NOT_DELETABLE);
        }

        userCouponRepository.deleteByCouponId(couponId);

        couponRepository.delete(coupon);
    }
}
