package com.mossy.member.app.cart;

import com.mossy.member.domain.cart.Cart;
import com.mossy.member.domain.market.MarketPolicy;
import com.mossy.member.out.cart.CartRepository;
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
