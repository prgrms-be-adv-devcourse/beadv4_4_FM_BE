package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.catalog.app.dto.CatalogDto;
import com.mossy.boundedContext.product.app.command.ProductOptionConfigureUseCase;
import com.mossy.boundedContext.product.app.query.GetProductDetailUseCase;
import com.mossy.boundedContext.product.app.query.ProductSummaryQueryService;
import com.mossy.boundedContext.product.app.command.RegisterProductUseCase;
import com.mossy.boundedContext.product.app.dto.ProductDataForEvent;
import com.mossy.boundedContext.catalog.app.query.CatalogQueryService;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.event.ProductRegisteredEvent;
import com.mossy.boundedContext.product.in.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.dto.response.ProductDetailResponse;
import com.mossy.global.eventPublisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final RegisterProductUseCase marketRegisterProductUseCase;
    private final ProductOptionConfigureUseCase productOptionConfigureUseCase;
    private final ProductSummaryQueryService productSummaryQueryService;
    private final GetProductDetailUseCase getProductDetailUseCase;
    private final EventPublisher eventPublisher;

    // 상품 등록
    @Transactional
    public Long registerProduct(ProductCreateRequest request) {
        // 구조 생성
        Product product = marketRegisterProductUseCase.create(
                request.sellerId(), request.catalogProductId(), request.basePrice()
        );

        // 옵션/아이템 등록
        productOptionConfigureUseCase.configure(product, request.optionGroups(), request.productItems());

        // 저장
        Long productId = marketRegisterProductUseCase.save(product);

        ProductDataForEvent summary = productSummaryQueryService.getCatalogSummary(
                product.getCatalogProductId(),
                productId
        );

        // 이벤트 발행
        eventPublisher.publish(new ProductRegisteredEvent(
                summary.catalogId(),
                summary.minPrice(),
                summary.sellerCount()));

        return productId;
    }

    // 상품 상세 정보 조회
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long catalogProductId) {
        ProductDetailResponse response = getProductDetailUseCase.execute(catalogProductId);
        return response;
    }

    // 상품 정보 수정
//    @Transactional
//    public void updateProduct(Long productId, Long currentSellerId, ProductUpdateRequest request) {
//        marketUpdateProductUseCase.update(productId, currentSellerId, request);
//    }

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
