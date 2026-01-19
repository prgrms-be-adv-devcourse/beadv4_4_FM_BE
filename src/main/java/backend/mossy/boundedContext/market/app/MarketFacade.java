package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.MarketUserDto;
import backend.mossy.shared.market.dto.requets.CartItemAddRequest;
import backend.mossy.shared.market.dto.requets.CartItemUpdateRequest;
import backend.mossy.shared.market.dto.requets.ProductRequest;
import backend.mossy.shared.member.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketFacade {
    private final MarketGetProductListUseCase marketGetProductListUseCase;
    private final MarketRegisterProductUseCase marketRegisterProductUseCase;
    private final MarketSyncUserUseCase marketSyncUserUseCase;
    private final MarketCartUseCase marketCartUseCase;

    @Transactional(readOnly = true)
    public List<Product> getProductList() {
        return marketGetProductListUseCase.getProductList();
    }

    @Transactional
    public Product registerProduct(ProductRequest request) {
        return marketRegisterProductUseCase.register(request);
    }

    @Transactional
    public MarketUser syncUser(UserDto user) {
        return marketSyncUserUseCase.syncUser(user);
    }

    @Transactional
    public RsData<Void> createCart(MarketUserDto buyer) {
        return marketCartUseCase.create(buyer);
    }

    @Transactional
    public RsData<Void> addCartItem(Long userId, CartItemAddRequest request) {
        return marketCartUseCase.addItem(userId, request);
    }

    @Transactional
    public RsData<Void> updateCartItem(Long userId, CartItemUpdateRequest request) {
        return marketCartUseCase.updateItem(userId, request);
    }

    @Transactional
    public RsData<Void> removeCartItem(Long userId, Long productId) {
        return marketCartUseCase.removeItem(userId, productId);
    }

    @Transactional
    public RsData<Void> clearCart(Long userId) {
        return marketCartUseCase.clear(userId);
    }
}
