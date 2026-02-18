package com.mossy.boundedContext.product.in;

import com.mossy.boundedContext.product.app.ProductFacade;
import com.mossy.boundedContext.product.in.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.dto.response.ProductDetailResponse;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 조회 및 관리 API")
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ApiV1ProductController {
    private final ProductFacade productFacade;

    // 상품 상세 정보
    @Operation(
            summary = "상품 상세 정보",
            description = "상품 상세 정보를 조회 합니다")
    @GetMapping("/{catalogProductId}")
    public RsData<ProductDetailResponse> getProductById(@PathVariable Long catalogProductId) {
        ProductDetailResponse response = productFacade.getProductDetail(catalogProductId);
        return new RsData<>("200", "", response);
    }

    // 상품 등록
    @Operation(
            summary = "상품 등록",
            description = "카탈로그 기반으로 새로운 판매 상품을 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<Long> createProduct(
            @RequestBody @Valid ProductCreateRequest request) {
        Long productId = productFacade.registerProduct(request);
        return new RsData<>("201", "상품이 등록되었습니다", productId);
    }

    // 상품 수정
//    @Operation(
//            summary = "상품 수정",
//            description = "상품을 수정합니다")
//    @PutMapping("/{productId}")
//    public RsData<Long> updateProduct(
//            @PathVariable Long productId,
//            @RequestHeader("X-Seller-Id") Long currentSellerId,
//            @ModelAttribute @Valid ProductUpdateRequest request) {
//        productFacade.updateProduct(productId, currentSellerId, request);
//
//        return new RsData<>("200", "상품이 수정되었습니다.", productId);
//    }

    // 상품 상태 수정
//    @Operation(
//            summary = "상품 상태 수정",
//            description = "상품 상태를 수정합니다.")
//    @PatchMapping("/{productId}/status")
//    public RsData<Long> changeStatus(
//            @PathVariable Long productId,
//            @RequestHeader("X-Seller-Id") Long currentSellerId,
//            @Valid @RequestBody ProductStatusUpdateRequest request) {
//        productFacade.changeProductStatus(productId, currentSellerId, request);
//
//        return new RsData<>("200", "상품 상태가 수정되었습니다.", productId);
//    }

    // 상품 삭제
//    @Operation(
//            summary = "상품 삭제",
//            description = "상품을 삭제합니다.")
//    @DeleteMapping("/{productId}")
//    public RsData<Long> deleteProduct(
//            @PathVariable Long productId,
//            @RequestHeader("X-Seller-Id") Long currentSellerId) {
//        productFacade.deleteProduct(productId, currentSellerId);
//
//        return new RsData<>("200", "상품이 삭제되었습니다.", productId);
//    }


}