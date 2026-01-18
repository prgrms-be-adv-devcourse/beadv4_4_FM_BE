package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.MarketFacade;
import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.requets.ProductCreateRequest;
import backend.mossy.shared.market.dto.requets.ProductStatusUpdateRequest;
import backend.mossy.shared.market.dto.requets.ProductUpdateRequest;
import backend.mossy.shared.market.dto.response.ProductDetailResponse;
import backend.mossy.shared.market.dto.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product/products")
@RequiredArgsConstructor
public class ApiV1ProductController {
    private final MarketFacade marketFacade;

    // 메인 화면 상품 리스트
    @GetMapping
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductList() {
        return marketFacade
                .getProductList()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    // 상품 상세 정보
    @GetMapping("/{productId}")
    public RsData<ProductDetailResponse> getProductById(@PathVariable Long productId) {
        ProductDetailResponse response = marketFacade.getProductById(productId);
        return new RsData<>("200", "", response);
    }

    // 상품 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<Void> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        marketFacade.registerProduct(request);
        return new RsData<>("201", "상품이 등록되었습니다");
    }

    // 상품 수정
    @PutMapping("/{productId}")
    public RsData<Long> updateProduct(
            @PathVariable Long productId,
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @RequestBody @Valid ProductUpdateRequest request) {
        marketFacade.updateProduct(productId, currentSellerId, request);

        return new RsData<>("200", "상품이 수정되었습니다.", productId);
    }

    // 상품 상태 수정
    @PatchMapping("/{productId}/status")
    public RsData<Long> changeStatus(
            @PathVariable Long productId,
            @RequestHeader("X-Seller-Id") Long currentSellerId,
            @Valid @RequestBody ProductStatusUpdateRequest request) {
        marketFacade.changeProductStatus(productId, currentSellerId, request);

        return new RsData<>("200", "상품 상태가 수정되었습니다.", productId);
    }

    @DeleteMapping("/{productId}")
    public RsData<Long> deleteProduct(
            @PathVariable Long productId,
            @RequestHeader("X-Seller-Id") Long currentSellerId) {
        marketFacade.deleteProduct(productId, currentSellerId);

        return new RsData<>("200", "상품이 삭제되었습니다.", productId);
    }
}
