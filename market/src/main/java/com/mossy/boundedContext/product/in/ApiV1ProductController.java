package com.mossy.boundedContext.product.in;

import com.mossy.boundedContext.product.app.ProductFacade;
import com.mossy.boundedContext.product.in.dto.request.ProductStatusUpdateRequest;
import com.mossy.boundedContext.product.in.dto.request.ProductUpdateRequest;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 조회 및 관리 API")
@RestController
@RequestMapping("/api/v1/product/products")
@RequiredArgsConstructor
public class ApiV1ProductController {
    private final ProductFacade productFacade;

    @GetMapping
    public RsData<String> test() {
        return new RsData<>("200", "hi");
    }

//    // 메인 화면 상품 리스트
//    @Operation(
//            summary = "메인 화면 상품",
//            description = "메인 화면 상품 리스트 조회합니다.")
//    @GetMapping
//    @Transactional(readOnly = true)
//    public RsData<Page<ProductResponse>> getProductList(
//            @RequestParam (defaultValue = "0") int page,
//            @RequestParam (defaultValue = "10") int size,
//            @RequestParam (defaultValue = "createdAt") String sort
//    ) {
//        System.out.println(("sort: " + sort));
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        Page<ProductResponse> productsList = productFacade
//                .getProductList(pageable)
//                .map(ProductResponse::from);
//        return new RsData<>("200", "", productsList);
//    }

//    // 상품 상세 정보
//    @Operation(
//            summary = "상품 상세 정보",
//            description = "상품 상세 정보를 조회 합니다")
//    @GetMapping("/{productId}")
//    public RsData<ProductDetailResponse> getProductById(@PathVariable Long productId) {
//        ProductDetailResponse response = productFacade.getProductById(productId);
//        return new RsData<>("200", "", response);
//    }

    // 상품 등록
//    @Operation(
//            summary = "상품 등록",
//            description = "상품 등록합니다.")
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public RsData<Void> createProduct(@ModelAttribute @Valid ProductCreateRequest request) {
//        productFacade.registerProduct(request);
//        return new RsData<>("201", "상품이 등록되었습니다");
//    }

    // 상품 수정
    @Operation(
            summary = "상품 수정",
            description = "상품을 수정합니다")
    @PutMapping("/{productId}")
    public RsData<Long> updateProduct(
            @PathVariable Long productId,
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @ModelAttribute @Valid ProductUpdateRequest request) {
        productFacade.updateProduct(productId, currentSellerId, request);

        return new RsData<>("200", "상품이 수정되었습니다.", productId);
    }

    // 상품 상태 수정
    @Operation(
            summary = "상품 상태 수정",
            description = "상품 상태를 수정합니다.")
    @PatchMapping("/{productId}/status")
    public RsData<Long> changeStatus(
            @PathVariable Long productId,
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @Valid @RequestBody ProductStatusUpdateRequest request) {
        productFacade.changeProductStatus(productId, currentSellerId, request);

        return new RsData<>("200", "상품 상태가 수정되었습니다.", productId);
    }

    // 상품 삭제
    @Operation(
            summary = "상품 삭제",
            description = "상품을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public RsData<Long> deleteProduct(
            @PathVariable Long productId,
            @RequestHeader("X-Seller-Id") Long currentSellerId) {
        productFacade.deleteProduct(productId, currentSellerId);

        return new RsData<>("200", "상품이 삭제되었습니다.", productId);
    }
}
