package com.mossy.boundedContext.coupon.in;

import com.mossy.boundedContext.coupon.app.CouponFacade;
import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Coupon", description = "쿠폰 관리 API")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class ApiV1CouponController {

    private final CouponFacade couponFacade;

    @Operation(
            summary = "판매자 쿠폰 생성",
            description = "판매자가 자신의 상품에 대한 쿠폰을 생성합니다."
    )
    @PostMapping("/seller")
    public RsData<Long> createSellerCoupon(
            @RequestParam(name = "sellerId") Long sellerId,
            @RequestBody @Valid CouponCreateRequest request
    ) {
        return RsData.success(SuccessCode.COUPON_CREATE, couponFacade.createSellerCoupon(sellerId, request));
    }

    @Operation(summary = "관리자 쿠폰 생성", description = "관리자가 쿠폰을 생성합니다.")
    @PostMapping("/admin")
    public RsData<Long> createAdminCoupon(
            @RequestParam(name = "adminId") Long adminId,
            @RequestBody @Valid CouponCreateRequest request
    ) {
        return RsData.success(SuccessCode.COUPON_CREATE, couponFacade.createAdminCoupon(adminId, request));
    }
}
