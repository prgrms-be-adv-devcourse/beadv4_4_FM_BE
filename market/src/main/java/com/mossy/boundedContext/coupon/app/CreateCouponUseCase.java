//package com.mossy.boundedContext.coupon.app;
//
//import com.mossy.boundedContext.coupon.domain.Coupon;
//import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
//import com.mossy.boundedContext.coupon.out.CouponRepository;
//import com.mossy.boundedContext.product.out.ProductApiClient;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class CreateCouponUseCase {
//    private final CouponRepository couponRepository;
//    private final ProductApiClient productApiClient;
//
//    @Transactional
//    public Long createCoupon(Long sellerId, CouponCreateRequest request) {
//        productApiClient.validateProductOwner(request.productId(), sellerId);
//
//        Coupon coupon = Coupon.create(
//                sellerId,
//                request.productId(),
//                request.couponName(),
//                request.couponType(),
//                request.discountValue(),
//                request.maxDiscountAmount(),
//                request.startAt(),
//                request.endAt()
//        );
//
//        Coupon savedCoupon = couponRepository.save(coupon);
//        return savedCoupon.getId();
//    }
//}
