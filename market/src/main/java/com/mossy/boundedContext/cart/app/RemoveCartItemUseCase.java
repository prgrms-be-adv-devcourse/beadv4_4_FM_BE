package com.mossy.boundedContext.cart.app;

import com.mossy.boundedContext.cart.domain.Cart;
import com.mossy.boundedContext.cart.out.CartRepository;
import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
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
