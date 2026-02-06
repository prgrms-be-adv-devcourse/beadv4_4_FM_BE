package com.mossy.boundedContext.cart.app;

import com.mossy.boundedContext.cart.in.dto.request.CartItemAddRequest;
import com.mossy.boundedContext.cart.in.dto.request.CartItemUpdateRequest;
import com.mossy.boundedContext.cart.in.dto.response.CartResponse;
import com.mossy.shared.market.dto.event.MarketUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartFacade {
    private final CreateCartUseCase createCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final GetCartItemListUseCase getCartItemListUseCase;

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        return getCartItemListUseCase.getCart(userId);
    }

    @Transactional
    public void createCart(MarketUserDto buyer) {
        createCartUseCase.create(buyer);
    }

    @Transactional
    public void addCartItem(Long userId, CartItemAddRequest request) {
        addCartItemUseCase.addItem(userId, request);
    }

    @Transactional
    public void updateCartItem(Long userId, CartItemUpdateRequest request) {
        updateCartItemUseCase.updateItemQuantity(userId, request);
    }

    @Transactional
    public void removeCartItem(Long userId, Long productId) {
        removeCartItemUseCase.removeItem(userId, productId);
    }

    @Transactional
    public void clearCart(Long userId) {
        clearCartUseCase.clear(userId);
    }
}
