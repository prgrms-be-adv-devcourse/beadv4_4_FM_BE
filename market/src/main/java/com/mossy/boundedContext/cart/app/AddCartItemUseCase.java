package com.mossy.boundedContext.cart.app;

import com.mossy.boundedContext.cart.domain.Cart;
import com.mossy.boundedContext.cart.in.dto.request.CartItemAddRequest;
import com.mossy.boundedContext.cart.out.CartRepository;
import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.marketUser.domain.MarketPolicy;
import com.mossy.boundedContext.product.out.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddCartItemUseCase {

    private final MarketPolicy marketPolicy;
    private final CartRepository cartRepository;
    private final ProductApiClient productApiClient;

    public void addItem(Long userId, CartItemAddRequest request) {
        if (!productApiClient.exists(request.productId())) {
            throw new DomainException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
                () -> new DomainException(ErrorCode.CART_NOT_FOUND));

        cart.addItem(request.productId(), request.quantity(), marketPolicy);
    }
}
