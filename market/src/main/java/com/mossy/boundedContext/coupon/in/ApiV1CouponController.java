package com.mossy.boundedContext.coupon.in;

import com.mossy.boundedContext.coupon.app.CouponFacade;
import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.in.dto.request.CouponCreateRequest;
import com.mossy.boundedContext.coupon.in.dto.request.CouponUpdateRequest;
import com.mossy.boundedContext.coupon.in.dto.response.CouponResponse;
import com.mossy.boundedContext.coupon.in.dto.response.SellerCouponListResponse;
import com.mossy.boundedContext.coupon.in.dto.response.UserCouponResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestHeader("X-Seller-Id") Long sellerId,
            @RequestBody @Valid CouponCreateRequest request
    ) {
        return RsData.success(SuccessCode.COUPON_CREATE, couponFacade.createSellerCoupon(sellerId, request));
    }

    @Operation(summary = "관리자 쿠폰 생성", description = "관리자가 쿠폰을 생성합니다.")
    @PostMapping("/admin")
    public RsData<Long> createAdminCoupon(
            @RequestHeader("X-User-Id") Long adminId,
            @RequestBody @Valid CouponCreateRequest request
    ) {
        return RsData.success(SuccessCode.COUPON_CREATE, couponFacade.createAdminCoupon(adminId, request));
    }

    @Operation(summary = "판매자 쿠폰 목록 조회", description = "판매자가 자신이 생성한 쿠폰 목록을 조회합니다.")
    @GetMapping("/seller/my")
    public RsData<List<SellerCouponListResponse>> getSellerCoupons(
            @RequestHeader("X-Seller-Id") Long sellerId
    ) {
        return RsData.success(SuccessCode.COUPON_LIST, couponFacade.getSellerCoupons(sellerId));
    }

    @Operation(summary = "판매자 쿠폰 수정", description = "판매자가 자신의 쿠폰을 수정합니다.")
    @PatchMapping("/seller/{couponId}")
    public RsData<Void> updateSellerCoupon(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @PathVariable Long couponId,
            @RequestBody @Valid CouponUpdateRequest request
    ) {
        couponFacade.updateSellerCoupon(sellerId, couponId, request);
        return RsData.success(SuccessCode.COUPON_UPDATE);
    }

    @Operation(summary = "판매자 쿠폰 비활성화", description = "판매자가 자신의 쿠폰을 비활성화합니다.")
    @PatchMapping("/seller/{couponId}/deactivate")
    public RsData<Void> deactivateSellerCoupon(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @PathVariable Long couponId
    ) {
        couponFacade.deactivateSellerCoupon(sellerId, couponId);
        return RsData.success(SuccessCode.COUPON_DEACTIVATE);
    }

    @Operation(summary = "다운로드 가능한 쿠폰 목록 조회", description = "상품에 적용 가능한 다운로드 가능 쿠폰 목록을 조회합니다.")
    @GetMapping
    public RsData<List<CouponResponse>> getDownloadableCoupons(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "productItemId") Long productItemId
    ) {
        return RsData.success(SuccessCode.COUPON_LIST, couponFacade.getDownloadableCoupons(productItemId, userId));
    }

    @Operation(summary = "쿠폰 다운로드", description = "사용자가 쿠폰을 다운로드합니다.")
    @PostMapping("/{couponId}/download")
    public RsData<Long> downloadCoupon(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long couponId
    ) {
        return RsData.success(SuccessCode.COUPON_DOWNLOAD, couponFacade.downloadCoupon(couponId, userId));
    }

    @Operation(summary = "내 쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
    @GetMapping("/me")
    public RsData<Page<UserCouponResponse>> getMyUserCoupons(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "status", required = false) UserCouponStatus status,
            @PageableDefault Pageable pageable
    ) {
        return RsData.success(SuccessCode.MY_COUPON_LIST, couponFacade.getMyUserCoupons(userId, status, pageable));
    }

    @Operation(summary = "주문 시 적용 가능한 쿠폰 조회", description = "주문 상품에 적용 가능한 보유 쿠폰 목록을 조회합니다.")
    @GetMapping("/applicable")
    public RsData<List<UserCouponResponse>> getApplicableCoupons(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "productItemIds") List<Long> productItemIds
    ) {
        return RsData.success(SuccessCode.APPLICABLE_COUPON_LIST, couponFacade.getApplicableCoupons(userId, productItemIds));
    }
}
