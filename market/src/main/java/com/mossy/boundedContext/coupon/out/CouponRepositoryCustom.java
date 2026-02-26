package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.in.dto.response.CouponResponse;
import com.mossy.boundedContext.coupon.in.dto.response.SellerCouponListResponse;
import com.mossy.boundedContext.coupon.out.dto.CouponStatistics;
import com.mossy.shared.market.enums.CouponType;
import com.mossy.shared.market.enums.IssuerType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponRepositoryCustom {

    List<CouponResponse> findDownloadableCoupons(Long productItemId, Long userId);

    CouponStatistics getSellerCouponStatistics(Long sellerId, IssuerType issuerType);

    List<SellerCouponListResponse> findSellerCouponsContentOnly(Long sellerId, IssuerType issuerType, String status, CouponType couponType, Pageable pageable);
}
