package backend.mossy.boundedContext.market.in.product;

import backend.mossy.boundedContext.market.app.product.ProductFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.request.ProductCreateRequest;
import backend.mossy.shared.market.dto.request.ProductStatusUpdateRequest;
import backend.mossy.shared.market.dto.request.ProductUpdateRequest;
import backend.mossy.shared.market.dto.response.ProductDetailResponse;
import backend.mossy.shared.market.dto.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product/products")
@RequiredArgsConstructor
public class ApiV1ProductController {
    private final ProductFacade productFacade;

    // 메인 화면 상품 리스트
    @GetMapping
    @Transactional(readOnly = true)
    public RsData<Page<ProductResponse>> getProductList(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductResponse> productsList = productFacade
                .getProductList(pageable)
                .map(ProductResponse::from);
        return new RsData<>("200", "", productsList);
    }

    // 상품 상세 정보
    @GetMapping("/{productId}")
    public RsData<ProductDetailResponse> getProductById(@PathVariable Long productId) {
        ProductDetailResponse response = productFacade.getProductById(productId);
        return new RsData<>("200", "", response);
    }

    // 상품 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<Void> createProduct(@ModelAttribute @Valid ProductCreateRequest request) {
        productFacade.registerProduct(request);
        return new RsData<>("201", "상품이 등록되었습니다");
    }

    // 상품 수정
    @PutMapping("/{productId}")
    public RsData<Long> updateProduct(
            @PathVariable Long productId,
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @ModelAttribute @Valid ProductUpdateRequest request) {
        productFacade.updateProduct(productId, currentSellerId, request);

        return new RsData<>("200", "상품이 수정되었습니다.", productId);
    }

    // 상품 상태 수정
    @PatchMapping("/{productId}/status")
    public RsData<Long> changeStatus(
            @PathVariable Long productId,
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @Valid @RequestBody ProductStatusUpdateRequest request) {
        productFacade.changeProductStatus(productId, currentSellerId, request);

        return new RsData<>("200", "상품 상태가 수정되었습니다.", productId);
    }

    @DeleteMapping("/{productId}")
    public RsData<Long> deleteProduct(
            @PathVariable Long productId,
            @RequestHeader("X-Seller-Id") Long currentSellerId) {
        productFacade.deleteProduct(productId, currentSellerId);

        return new RsData<>("200", "상품이 삭제되었습니다.", productId);
    }
}
