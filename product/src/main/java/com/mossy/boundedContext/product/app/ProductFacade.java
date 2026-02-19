package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.product.app.command.RegisterProductUseCase;
import com.mossy.boundedContext.product.app.command.UpdateProductUseCase;
import com.mossy.boundedContext.product.app.dto.ProductDataForEvent;
import com.mossy.boundedContext.product.app.query.GetProductDetailUseCase;
import com.mossy.boundedContext.product.app.query.ProductSummaryQueryService;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.event.ProductPriceChangedEvent;
import com.mossy.boundedContext.product.in.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.dto.request.ProductUpdateRequest;
import com.mossy.boundedContext.product.in.dto.response.ProductDetailResponse;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.global.eventPublisher.EventPublisher;
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
    private final EventPublisher eventPublisher;

    // 상품 등록
    @Transactional
    public Long registerProduct(Long currentSellerId, ProductCreateRequest request) {

        Long productId = registerProductUseCase.register(request);

        ProductDataForEvent summary = productSummaryQueryService.getCatalogSummary(
                request.catalogProductId(),
                productId
        );

        // 이벤트 발행
        eventPublisher.publish(new ProductPriceChangedEvent(
                summary.catalogId(),
                summary.minPrice(),
                summary.sellerCount()));

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

        ProductDataForEvent summary = productSummaryQueryService.getCatalogSummary(
                product.getCatalogProductId(),
                productId
        );

        eventPublisher.publish(new ProductPriceChangedEvent(
                summary.catalogId(),
                summary.minPrice(),
                summary.sellerCount()
        ));
    }

//    // 상품 상태 직접 변경 (판매자 조작)
//    @Transactional
//    public void changeProductStatus(Long productId, Long currentSellerId, ProductStatusUpdateRequest request) {
//        marketChangeProductStatusUseCase.changeStatus(productId, currentSellerId, request);
//    }

//    // 상품 삭제
//    @Transactional
//    public void deleteProduct(Long productId, Long currentSellerId) {
//        marketDeleteProductUseCase.delete(productId, currentSellerId);
//    }

//    // 결제 시 재고 감소
//    @Transactional
//    public void decreaseProductStock(Long productId, Integer quantity) {
//        marketDecreaseStockUseCase.decrease(productId, quantity);
//    }
}
