package com.mossy.boundedContext.cart.app;

import com.mossy.boundedContext.cart.domain.Cart;
import com.mossy.boundedContext.cart.out.CartRepository;
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
