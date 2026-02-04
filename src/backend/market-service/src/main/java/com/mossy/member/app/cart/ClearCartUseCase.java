package com.mossy.member.app.cart;

import com.mossy.member.domain.cart.Cart;
import com.mossy.member.out.cart.CartRepository;
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
