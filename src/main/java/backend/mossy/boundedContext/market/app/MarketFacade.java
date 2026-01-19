package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.shared.market.dto.event.MarketUserDto;
import backend.mossy.shared.market.dto.request.*;
import backend.mossy.shared.market.dto.response.CartResponse;
import backend.mossy.shared.market.dto.response.ProductDetailResponse;
import backend.mossy.shared.member.dto.common.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketFacade {
    private final MarketGetProductListUseCase marketGetProductListUseCase;
    private final MarketGetProductDetailUseCase marketGetProductDetailUseCase;
    private final MarketRegisterProductUseCase marketRegisterProductUseCase;
    private final MarketUpdateProductUseCase marketUpdateProductUseCase;
    private final MarketChangeProductStatusUseCase marketChangeProductStatusUseCase;
    private final MarketDecreaseStockUseCase marketDecreaseStockUseCase;
    private final MarketDeleteProductUseCase marketDeleteProductUseCase;
    private final MarketSyncUserUseCase marketSyncUserUseCase;
    private final CartUseCase cartUseCase;

    // 메인 화면 상품 리스트
    @Transactional(readOnly = true)
    public List<Product> getProductList() {
        return marketGetProductListUseCase.getProductList();
    }

    // 상품 상세 정보 조회
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductById(Long productId) {
        Product product = marketGetProductDetailUseCase.execute(productId);
        return ProductDetailResponse.from(product);
    }

    // 상품 등록
    @Transactional
    public Product registerProduct(ProductCreateRequest request) {
        return marketRegisterProductUseCase.register(request);
    }

    // 상품 정보 수정
    @Transactional
    public void updateProduct(Long productId, Long currentSellerId, ProductUpdateRequest request) {
        marketUpdateProductUseCase.update(productId, currentSellerId, request);
    }

    // 상품 상태 직접 변경 (판매자 조작)
    @Transactional
    public void changeProductStatus(Long productId, Long currentSellerId, ProductStatusUpdateRequest request) {
        marketChangeProductStatusUseCase.changeStatus(productId, currentSellerId, request);
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId, Long currentSellerId) {
        marketDeleteProductUseCase.delete(productId,  currentSellerId);
    }

    // 결제 시 재고 감소
    @Transactional
    public void decreaseProductStock(Long productId, Integer quantity) {
        marketDecreaseStockUseCase.decrease(productId, quantity);
    }

    @Transactional
    public MarketUser syncUser(UserDto user) {
        return marketSyncUserUseCase.syncUser(user);
    }

    @Transactional
    public void createCart(MarketUserDto buyer) {
        cartUseCase.create(buyer);
    }

    @Transactional
    public void addCartItem(Long userId, CartItemAddRequest request) {
        cartUseCase.addItem(userId, request);
    }

    @Transactional
    public void updateCartItem(Long userId, CartItemUpdateRequest request) {
        cartUseCase.updateItem(userId, request);
    }

    @Transactional
    public void removeCartItem(Long userId, Long productId) {
        cartUseCase.removeItem(userId, productId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartUseCase.clear(userId);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        return cartUseCase.getCart(userId);
    }
}
