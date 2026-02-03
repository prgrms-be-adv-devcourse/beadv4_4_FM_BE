package com.mossy.boundedContext.app.cart;

import com.mossy.boundedContext.domain.cart.Cart;
import com.mossy.boundedContext.domain.market.MarketPolicy;
import com.mossy.boundedContext.out.cart.CartRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.shared.market.dto.request.CartItemUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCartItemQuantityUseCase {

    private final CartRepository cartRepository;
    private final MarketPolicy marketPolicy;

    public void updateItemQuantity(Long userId, CartItemUpdateRequest request) {
        Cart cart = cartRepository.findByBuyerId(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.CART_NOT_FOUND));

        cart.updateItemQuantity(request.productId(), request.quantity(), marketPolicy);
    }
}
