package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.in.dto.request.CouponUpdateRequest;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateSellerCouponUseCase {

    private final CouponRepository couponRepository;

    @Transactional
    public void update(Long sellerId, Long couponId, CouponUpdateRequest request) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new DomainException(ErrorCode.COUPON_NOT_FOUND));

        coupon.validateOwnerSeller(sellerId);

        coupon.update(
                request.couponName(),
                request.discountValue(),
                request.maxDiscountAmount(),
                request.endAt()
        );
    }
}
