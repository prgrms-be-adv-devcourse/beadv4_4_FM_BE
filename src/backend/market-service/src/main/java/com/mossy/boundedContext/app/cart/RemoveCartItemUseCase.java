package com.mossy.boundedContext.app.cart;

import com.mossy.boundedContext.domain.cart.Cart;
import com.mossy.boundedContext.out.cart.CartRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoveCartItemUseCase {

    private final CartRepository cartRepository;

    public void removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByBuyerId(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.CART_NOT_FOUND));

        cart.removeItem(productId);
    }
}
