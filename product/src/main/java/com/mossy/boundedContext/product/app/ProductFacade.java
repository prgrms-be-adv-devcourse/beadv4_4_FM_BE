package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.product.app.command.*;
import com.mossy.boundedContext.product.app.dto.ProductDataForEvent;
import com.mossy.boundedContext.product.app.query.GetProductDetailUseCase;
import com.mossy.boundedContext.product.app.query.ProductSummaryQueryService;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.event.ProductCatalogSyncEvent;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductUpdateRequest;
import com.mossy.boundedContext.product.in.rest.dto.response.ProductDetailResponse;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.product.enums.ProductItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final RegisterProductUseCase registerProductUseCase;
    private final ProductSummaryQueryService productSummaryQueryService;
    private final GetProductDetailUseCase getProductDetailUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ProductRepository productRepository;
    private final UpdateProductItemStatusUseCase updateProductItemStatusUseCase;
    private final DeleteProductItemUseCase deleteProductItemUseCase;
    private final EventPublisher eventPublisher;

    // 상품 등록
    @Transactional
    public Long registerProduct(Long currentSellerId, ProductCreateRequest request) {

        Long productId = registerProductUseCase.register(request);

        publishCatalogSyncEvent(request.catalogProductId());

        return productId;
    }

    // 상품 상세 정보 조회
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long catalogProductId) {
        return getProductDetailUseCase.execute(catalogProductId);
    }

    // 상품 정보 수정
    @Transactional
    public void updateProduct(Long productId, Long currentSellerId, ProductUpdateRequest request) {
        Product product = updateProductUseCase.updateProduct(productId, currentSellerId, request);

        productRepository.flush();

        publishCatalogSyncEvent(product.getCatalogProductId());
    }

    // 상품 상태 직접 변경 (판매자 조작)
    @Transactional
    public void changeProductItemStatus(Long productId, Long currentSellerId, Long productItemId, ProductItemStatus status) {
        Product product = updateProductItemStatusUseCase.execute(
                new UpdateProductItemStatusCommand(currentSellerId, productId, productItemId, status)
        );

        publishCatalogSyncEvent(product.getCatalogProductId());
    }

    // 상품 아이템 삭제
    @Transactional
    public void deleteProductItem(Long sellerId, Long productId, Long itemId) {
        Product product = deleteProductItemUseCase.execute(sellerId, productId, itemId);
        publishCatalogSyncEvent(product.getCatalogProductId());
    }

//    // 결제 시 재고 감소
//    @Transactional
//    public void decreaseProductStock(Long productId, Integer quantity) {
//        marketDecreaseStockUseCase.decrease(productId, quantity);
//    }

    // 엘라스틱서치 동기화 이벤트
    private void publishCatalogSyncEvent(Long catalogProductId) {
        ProductDataForEvent summary = productSummaryQueryService.getCatalogSummary(catalogProductId);

        eventPublisher.publish(new ProductCatalogSyncEvent(
                summary.catalogId(),
                summary.minPrice(),
                summary.sellerCount(),
                summary.minPriceProductItemId()
        ));
    }
}
