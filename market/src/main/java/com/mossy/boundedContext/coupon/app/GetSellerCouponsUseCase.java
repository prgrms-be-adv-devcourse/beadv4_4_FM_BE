package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.in.dto.response.SellerCouponListResponse;
import com.mossy.boundedContext.coupon.in.dto.response.SellerCouponPageResponse;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import com.mossy.boundedContext.coupon.out.dto.CouponStatistics;
import com.mossy.shared.market.enums.CouponType;
import com.mossy.shared.market.enums.IssuerType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSellerCouponsUseCase {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public SellerCouponPageResponse getSellerCoupons(
            Long sellerId,
            String status,
            CouponType couponType,
            Pageable pageable
    ) {
        CouponStatistics stats = couponRepository.getSellerCouponStatistics(sellerId, IssuerType.SELLER);
        List<SellerCouponListResponse> content = couponRepository.findSellerCouponsContentOnly(
                sellerId,
                IssuerType.SELLER,
                status,
                couponType,
                pageable);

        return SellerCouponPageResponse.of(content, pageable, stats);
    }
}
