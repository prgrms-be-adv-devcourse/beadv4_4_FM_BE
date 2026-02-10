package com.mossy.boundedContext.cart.app;

import com.mossy.boundedContext.cart.domain.Cart;
import com.mossy.boundedContext.cart.out.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClearCartUseCase {

    private final CartRepository cartRepository;

    @Transactional
    public void clear(Long userId) {
        cartRepository.findByBuyerId(userId).ifPresent(Cart::clear);
    }
}
