package com.mossy.boundedContext.product.in.internal;

import com.mossy.boundedContext.catalog.app.CatalogFacade;
import com.mossy.boundedContext.catalog.app.dto.CatalogReviewInfoDto;
import com.mossy.boundedContext.product.app.ProductFacade;
import com.mossy.boundedContext.product.in.internal.dto.request.StockCheckRequest;
import com.mossy.boundedContext.product.in.internal.dto.response.ProductInfoResponse;
import com.mossy.boundedContext.product.in.internal.dto.response.ProductResponse;
import com.mossy.boundedContext.product.in.internal.dto.response.ReviewProductInfoResponse;
import com.mossy.boundedContext.product.in.internal.dto.response.WishlistProductResponse;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product", description = "FeignClient Controller")
@RestController
@RequestMapping("/internal/v1/products")
@RequiredArgsConstructor
public class InternalProductV1Controller {
    private final ProductFacade productFacade;
    private final CatalogFacade  catalogFacade;

    // 재고 감소
    @PostMapping("/decrease")
    public void decreaseStock(@RequestBody List<StockCheckRequest> items) {
        productFacade.decreaseStock(items);
    }

    // 재고 복수
    @PostMapping("/increase")
    public void increaseStock(@RequestBody List<StockCheckRequest> items) {
        productFacade.increaseStock(items);
    };

    // 위시리스트
    @GetMapping("/wishlists")
    public List<WishlistProductResponse> getWishlists(
            @RequestParam("productItemIds") List<Long> productItemIds
    ) {
        return productFacade.getWishlistProductItems(productItemIds);
    }

    // 장바구니
    @GetMapping("/carts")
    public List<ProductInfoResponse> getCarts(
            @RequestParam("productItemIds") List<Long> productItemIds
    ) {
        return productFacade.getCartProductItems(productItemIds);
    }

    // 리뷰
    @GetMapping("/reviews")
    public ReviewProductInfoResponse getReviewProductInfo(
            @RequestParam("productItemId") Long productItemId
    ) {
        Long catalogId = productFacade.getCatalogIdByProductItemId(productItemId);

        CatalogReviewInfoDto catalogInfo = catalogFacade.getReviewProductInfo(catalogId);

        return new ReviewProductInfoResponse(
                catalogInfo.name(),
                catalogInfo.thumbnail()
        );
    }

    // 필터 정렬
    @PostMapping("/filter-by-reviews")
    public List<ProductResponse> filterByReviews(
            @RequestBody List<Long> productIds
    ) {
        return productFacade.filterByReviews(productIds);
    }

    // 추천 상품 정보
    @PostMapping("/details")
    public List<ProductResponse> getProductDetails(@RequestBody List<Long> productIds) {
        return productFacade.getProductDetails(productIds);
    }
}
