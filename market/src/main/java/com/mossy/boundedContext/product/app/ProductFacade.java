package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.dto.request.ProductStatusUpdateRequest;
import com.mossy.boundedContext.product.in.dto.request.ProductUpdateRequest;
import com.mossy.boundedContext.product.in.dto.response.ProductDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    //private final MarketGetProductListUseCase marketGetProductListUseCase;
    //private final MarketGetProductDetailUseCase marketGetProductDetailUseCase;
    private final MarketRegisterProductUseCase marketRegisterProductUseCase;
    private final ProductOptionConfigureUseCase productOptionConfigureUseCase;
   // private final MarketUpdateProductUseCase marketUpdateProductUseCase;
    //private final MarketChangeProductStatusUseCase marketChangeProductStatusUseCase;
    //private final MarketDecreaseStockUseCase marketDecreaseStockUseCase;
    //private final MarketDeleteProductUseCase marketDeleteProductUseCase;

    // 메인 화면 상품 리스트
//    @Transactional(readOnly = true)
//    public Page<Product> getProductList(Pageable pageable) {
//        return marketGetProductListUseCase.getProductList(pageable);
//    }

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

        // Step 3: 최종 저장 (DB 반영)
        return marketRegisterProductUseCase.save(product);
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
