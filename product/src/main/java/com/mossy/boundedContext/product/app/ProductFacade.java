package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.product.app.command.*;
import com.mossy.boundedContext.product.app.query.GetProductDetailUseCase;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.internal.dto.request.StockCheckRequest;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductUpdateRequest;
import com.mossy.boundedContext.product.in.rest.dto.response.ProductDetailResponse;
import com.mossy.shared.product.enums.ProductItemStatus;
import com.mossy.shared.product.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // 상품 등록
    public Long registerProduct(Long currentSellerId, ProductCreateRequest request) {

        Long productId = registerProductUseCase.register(request);

        return productId;
    }

    // 상품 상세 정보 조회
    public ProductDetailResponse getProductDetail(Long catalogProductId) {
        return getProductDetailUseCase.execute(catalogProductId);
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
}
