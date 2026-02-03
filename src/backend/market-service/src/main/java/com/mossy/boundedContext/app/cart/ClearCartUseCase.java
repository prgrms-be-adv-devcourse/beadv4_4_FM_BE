package com.mossy.boundedContext.app.cart;

import com.mossy.boundedContext.domain.cart.Cart;
import com.mossy.boundedContext.out.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClearCartUseCase {

    private final CartRepository cartRepository;

    public void clear(Long userId) {
        cartRepository.findByBuyerId(userId).ifPresent(Cart::clear);
    }
}
