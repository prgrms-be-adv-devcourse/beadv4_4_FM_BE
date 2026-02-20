package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.domain.IssuerType;
import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAdminCouponUseCase {

    private final CouponRepository couponRepository;

    @Transactional
    public Long create(Long adminId, CouponCreateRequest request) {
        Coupon coupon = Coupon.create(
                adminId,
                IssuerType.ADMIN,
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
