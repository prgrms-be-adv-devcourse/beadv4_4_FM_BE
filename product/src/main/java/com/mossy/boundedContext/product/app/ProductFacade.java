package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.product.app.command.*;
import com.mossy.boundedContext.product.app.query.*;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.internal.dto.request.StockCheckRequest;
import com.mossy.boundedContext.product.in.internal.dto.response.ProductInfoResponse;
import com.mossy.boundedContext.product.in.internal.dto.response.ProductResponse;
import com.mossy.boundedContext.product.in.internal.dto.response.WishlistProductResponse;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductUpdateRequest;
import com.mossy.boundedContext.product.in.rest.dto.response.ProductDetailResponse;
import com.mossy.shared.product.enums.ProductItemStatus;
import com.mossy.shared.product.enums.ProductStatus;
import com.mossy.shared.product.event.ProductClickedEvent;
import com.mossy.kafka.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final RegisterProductUseCase registerProductUseCase;
    private final GetProductDetailUseCase getProductDetailUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateProductItemStatusUseCase updateProductItemStatusUseCase;
    private final UpdateProductStatusUseCase updateProductStatusUseCase;
    private final DeleteProductItemUseCase deleteProductItemUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final DecreaseStockUseCase decreaseStockUseCase;
    private final IncreaseStockUsecase increaseStockUseCase;
    private final GetWishlistProductsUseCase getWishlistProductsUseCase;
    private final GetCartProductDetailsUseCase getCartProductDetailsUseCase;
    private final GetCatalogIdUseCase getCatalogIdUseCase;
    private final FilterProductsByReviewsUseCase filterProductsByReviewsUseCase;
    private final GetProductDetailsUseCase getProductDetailsUseCase;
    private final KafkaEventPublisher kafkaEventPublisher;

    // 상품 등록
    public Long registerProduct(Long currentSellerId, ProductCreateRequest request) {

        Long productId = registerProductUseCase.register(request);

        return productId;
    }

    // 상품 상세 정보 조회
    public ProductDetailResponse getProductDetail(Long catalogProductId, Long userId) {
        ProductDetailResponse response = getProductDetailUseCase.execute(catalogProductId);

        // 로그인 유저일 경우 클릭 이벤트 카프카 발행
        if (userId != null) {
            try {
                kafkaEventPublisher.publish(new ProductClickedEvent(userId, catalogProductId));
            } catch (Exception e) {
                log.warn("상품 클릭 이벤트 발행 실패: userId={}, productId={}", userId, catalogProductId, e);
            }
        }

        return response;
    }

    // 상품 정보 수정
    public void updateProduct(Long productId, Long currentSellerId, ProductUpdateRequest request) {
        updateProductUseCase.updateProduct(productId, currentSellerId, request);
    }

    // 상품 상태 변경 (판매자)
    public void changeProductStatus(Long productId, Long currentSellerId, ProductStatus newStatus) {
        updateProductStatusUseCase.execute(
                new UpdateProductStatusCommand(productId, currentSellerId, newStatus));
    }

    // 상품 삭제
    public void deleteProduct(Long productId, Long currentSellerId) {
        deleteProductUseCase.execute(productId, currentSellerId);
    }

    // 상품 아이템 상태 변경 (판매자)
    public void changeProductItemStatus(Long productId, Long currentSellerId, Long productItemId, ProductItemStatus status) {
        Product product = updateProductItemStatusUseCase.execute(
                new UpdateProductItemStatusCommand(currentSellerId, productId, productItemId, status)
        );
    }

    // 상품 아이템 삭제
    public void deleteProductItem(Long sellerId, Long productId, Long itemId) {
        deleteProductItemUseCase.execute(sellerId, productId, itemId);
    }

    // 재고 차감
    public void decreaseStock(List<StockCheckRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            return;
        }
        decreaseStockUseCase.execute(requests);
    }

    // 재고 복구
    public void increaseStock(List<StockCheckRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }
        increaseStockUseCase.execute(requests);
    }

    // 위시리스트
    public List<WishlistProductResponse> getWishlistProductItems(List<Long> productIds) {
        return getWishlistProductsUseCase.execute(productIds);
    }

    // 장바구니
    public List<ProductInfoResponse> getCartProductItems(List<Long> productItemIds) {
        return getCartProductDetailsUseCase.execute(productItemIds);
    }

    public Long getCatalogIdByProductItemId(Long productItemId) {
        return getCatalogIdUseCase.execute(productItemId);
    }

    // ai 추천 리뷰로 필터
    public List<ProductResponse> filterByReviews(List<Long> productIds) {
        return filterProductsByReviewsUseCase.execute(productIds);
    }

    // 추천 상품 정보
    public List<ProductResponse> getProductDetails(List<Long> productIds) {
        return getProductDetailsUseCase.execute(productIds);
    }
}
