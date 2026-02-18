package com.mossy.boundedContext.cart.app;

import com.mossy.boundedContext.cart.domain.Cart;
import com.mossy.boundedContext.cart.in.dto.request.CartItemAddRequest;
import com.mossy.boundedContext.cart.out.CartRepository;
import com.mossy.boundedContext.marketUser.domain.MarketPolicy;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddCartItemUseCase {

    private final MarketPolicy marketPolicy;
    private final CartRepository cartRepository;

    @Transactional
    public void addItem(Long userId, CartItemAddRequest request) {
        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
                () -> new DomainException(ErrorCode.CART_NOT_FOUND));

        cart.addItem(request.productItemId(), request.quantity(), marketPolicy);
    }
}
