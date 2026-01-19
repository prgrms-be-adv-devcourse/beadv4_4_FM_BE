package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.app.marketUser.MarketSyncUserUseCase;
import backend.mossy.shared.market.dto.event.MarketUserDto;
import backend.mossy.shared.market.dto.request.CartItemAddRequest;
import backend.mossy.shared.market.dto.request.CartItemUpdateRequest;
import backend.mossy.shared.market.dto.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartFacade {
    private final MarketSyncUserUseCase marketSyncUserUseCase;
    private final CartUseCase cartUseCase;

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
