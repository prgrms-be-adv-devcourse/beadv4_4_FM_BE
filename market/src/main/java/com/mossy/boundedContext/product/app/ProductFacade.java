package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.product.domain.CatalogDocument;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.event.ProductRegisteredEvent;
import com.mossy.boundedContext.product.in.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.dto.request.ProductStatusUpdateRequest;
import com.mossy.boundedContext.product.in.dto.request.ProductUpdateRequest;
import com.mossy.boundedContext.product.in.dto.response.ProductDetailResponse;
import com.mossy.global.eventPublisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    //private final MarketGetProductDetailUseCase marketGetProductDetailUseCase;
    private final MarketRegisterProductUseCase marketRegisterProductUseCase;
    private final ProductOptionConfigureUseCase productOptionConfigureUseCase;
   // private final MarketUpdateProductUseCase marketUpdateProductUseCase;
    //private final MarketChangeProductStatusUseCase marketChangeProductStatusUseCase;
    //private final MarketDecreaseStockUseCase marketDecreaseStockUseCase;
    //private final MarketDeleteProductUseCase marketDeleteProductUseCase;
    private final EventPublisher eventPublisher;

    // 상품 상세 정보 조회
//    @Transactional(readOnly = true)
//    public ProductDetailResponse getProductById(Long productId) {
//        Product product = marketGetProductDetailUseCase.execute(productId);
//        return ProductDetailResponse.from(product);
//    }

    // 상품 등록
    @Transactional
    public Long registerProduct(ProductCreateRequest request) {
        // Step 1: 뼈대 생성 (Memory 상의 객체)
        Product product = marketRegisterProductUseCase.create(
                request.sellerId(), request.catalogProductId(), request.basePrice()
        );

        // Step 2: 옵션/아이템 조립 (객체 그래프 완성)
        productOptionConfigureUseCase.configure(product, request.optionGroups(), request.productItems());

        // Step 3: 최종 저장
        Long productId = marketRegisterProductUseCase.save(product);

        // 이벤트 발행
        eventPublisher.publish(new ProductRegisteredEvent(product.getCatalogProductId()));

        return productId;
    }

    // 상품 정보 수정
//    @Transactional
//    public void updateProduct(Long productId, Long currentSellerId, ProductUpdateRequest request) {
//        marketUpdateProductUseCase.update(productId, currentSellerId, request);
//    }
//
//    // 상품 상태 직접 변경 (판매자 조작)
//    @Transactional
//    public void changeProductStatus(Long productId, Long currentSellerId, ProductStatusUpdateRequest request) {
//        marketChangeProductStatusUseCase.changeStatus(productId, currentSellerId, request);
//    }
//
//    // 상품 삭제
//    @Transactional
//    public void deleteProduct(Long productId, Long currentSellerId) {
//        marketDeleteProductUseCase.delete(productId, currentSellerId);
//    }
//
//    // 결제 시 재고 감소
//    @Transactional
//    public void decreaseProductStock(Long productId, Integer quantity) {
//        marketDecreaseStockUseCase.decrease(productId, quantity);
//    }
}
