package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.shared.market.enums.IssuerType;
import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSellerCouponUseCase {

    private final CouponRepository couponRepository;

    @Transactional
    public Long create(Long sellerId, CouponCreateRequest request) {
        Coupon coupon = Coupon.create(
                sellerId,
                IssuerType.SELLER,
                request.productItemId(),
                request.couponName(),
                request.couponType(),
                request.discountValue(),
                request.maxDiscountAmount(),
                request.startAt(),
                request.endAt()
        );

        return couponRepository.save(coupon).getId();
    }
}
