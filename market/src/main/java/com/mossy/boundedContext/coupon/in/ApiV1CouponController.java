//package com.mossy.boundedContext.coupon.in;
//
//import com.mossy.boundedContext.coupon.app.CouponFacade;
//import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
//import com.mossy.global.rsData.RsData;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@Tag(name = "Coupon", description = "쿠폰 관리 API")
//@RestController
//@RequestMapping("/api/v1/coupons")
//@RequiredArgsConstructor
//public class ApiV1CouponController {
//    private final CouponFacade couponFacade;
//
//    @Operation(
//            summary = "쿠폰 생성",
//            description = "새로운 쿠폰을 생성합니다.")
//    @PostMapping
//    public RsData<Long> createCoupon(
//            @RequestParam(name = "sellerId") Long sellerId,
//            @RequestBody @Valid CouponCreateRequest request
//    ) {
//        Long couponId = couponFacade.createCoupon(sellerId, request);
//        return new RsData<>("200", "쿠폰이 생성되었습니다.", couponId);
//    }
//}
