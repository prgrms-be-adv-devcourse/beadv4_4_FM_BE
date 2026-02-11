package com.mossy.boundedContext.cart.app;

import com.mossy.boundedContext.cart.domain.Cart;
import com.mossy.boundedContext.cart.out.CartRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoveCartItemUseCase {

    private final CartRepository cartRepository;

    @Transactional
    public void removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByBuyerId(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.CART_NOT_FOUND));

        cart.removeItem(productId);
    }

    @Transactional
    public void removeItems(Long userId, List<Long> productIds) {
        Cart cart = cartRepository.findByBuyerId(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.CART_NOT_FOUND));

        cart.removeItems(productIds);
    }
}
