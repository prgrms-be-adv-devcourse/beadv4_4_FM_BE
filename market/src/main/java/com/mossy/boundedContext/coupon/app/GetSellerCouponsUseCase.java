package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.shared.market.enums.IssuerType;
import com.mossy.boundedContext.coupon.in.dto.response.SellerCouponListResponse;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSellerCouponsUseCase {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public List<SellerCouponListResponse> getSellerCoupons(Long sellerId) {
        List<Coupon> coupons = couponRepository.findByIssuerIdAndIssuerTypeOrderByCreatedAtDesc(
                sellerId,
                IssuerType.SELLER
        );

        return coupons.stream()
                .map(SellerCouponListResponse::from)
                .toList();
    }
}
