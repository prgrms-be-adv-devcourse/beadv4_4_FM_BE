package com.mossy.boundedContext.product.in.rest;

import com.mossy.boundedContext.product.app.ProductFacade;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductStatusUpdateRequest;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductUpdateRequest;
import com.mossy.boundedContext.product.in.rest.dto.request.UpdateProductItemStatusRequest;
import com.mossy.boundedContext.product.in.rest.dto.response.ProductDetailResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 조회 및 관리 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ApiV1ProductController {
    private final ProductFacade productFacade;

    // 상품 상세 정보
    @Operation(
            summary = "상품 상세 정보",
            description = "상품 상세 정보를 조회 합니다")
    @GetMapping("/{catalogProductId}")
    public RsData<ProductDetailResponse> getProductById(
            @PathVariable Long catalogProductId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        ProductDetailResponse response = productFacade.getProductDetail(catalogProductId, userId);
        return RsData.success(SuccessCode.GET_PRODUCT_SUCCESS, response);
    }

    // 상품 등록
    @Operation(
            summary = "상품 등록",
            description = "카탈로그 기반으로 새로운 판매 상품을 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<Long> createProduct(
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @RequestBody @Valid ProductCreateRequest request) {
        Long productId = productFacade.registerProduct(currentSellerId, request);
        return RsData.success(SuccessCode.CREATE_PRODUCT_SUCCESS, productId);
    }

    // 상품 수정
    @Operation(
            summary = "상품 수정",
            description = "상품을 수정합니다")
    @PutMapping("/{productId}")
    public RsData<Long> updateProduct(
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @PathVariable Long productId,
            @RequestBody @Valid ProductUpdateRequest request) {
        productFacade.updateProduct(productId, currentSellerId, request);

        return RsData.success(SuccessCode.UPDATE_PRODUCT_SUCCESS, productId);
    }

    // 상품 상태 수정
    @PatchMapping("/{productId}/status")
    public RsData<Long> changeStatus(
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @PathVariable Long productId,
            @RequestBody @Valid ProductStatusUpdateRequest request
    ) {
        productFacade.changeProductStatus(productId, currentSellerId, request.status());
        return RsData.success(SuccessCode.UPDATE_PRODUCT_SUCCESS, productId);
    }

    @Operation(
            summary = "상품 삭제",
            description = "상품 삭제합니다.")
    @DeleteMapping("/{productId}")
    public RsData<Long> deleteProduct(
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @PathVariable Long productId
    ) {
        productFacade.deleteProduct(currentSellerId, productId);

        return RsData.success(SuccessCode.DELETE_PRODUCT_ITEM_SUCCESS, productId);
    }

    // 상품 아이템 상태 수정
    @Operation(
            summary = "상품 아이템 상태 수정",
            description = "상품 아이템 상태를 수정합니다.")
    @PatchMapping("/{productId}/items/{productItemId}/status")
    public RsData<Long> updateProductItemStatus(
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @PathVariable Long productId,
            @PathVariable Long productItemId,
            @Valid @RequestBody UpdateProductItemStatusRequest request
    ) {
        productFacade.changeProductItemStatus(productId, currentSellerId, productItemId, request.status());
        return RsData.success(SuccessCode.UPDATE_PRODUCT_ITEM_STATUS_SUCCESS, productItemId);
    }

    // 상품 아이템 삭제
    @Operation(
            summary = "상품 아이템 삭제",
            description = "상품 아이템을 삭제합니다.")
    @DeleteMapping("/{productId}/items/{productItemId}")
    public RsData<Long> deleteProductItem(
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @PathVariable Long productId,
            @PathVariable Long productItemId
    ) {
        productFacade.deleteProductItem(currentSellerId, productId, productItemId);

        return RsData.success(SuccessCode.DELETE_PRODUCT_ITEM_SUCCESS, productId);
    }

}