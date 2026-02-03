package com.mossy.boundedContext.app.cart;

import com.mossy.boundedContext.domain.cart.Cart;
import com.mossy.boundedContext.domain.market.MarketPolicy;
import com.mossy.boundedContext.out.cart.CartRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.shared.market.dto.request.CartItemAddRequest;
import com.mossy.shared.market.out.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddCartItemUseCase {

    private final MarketPolicy marketPolicy;
    private final CartRepository cartRepository;
    private final ProductApiClient productApiClient;

//    public void addItem(Long userId, CartItemAddRequest request) {
//        if (!productApiClient.exists(request.productId())) {
//            throw new DomainException(ErrorCode.PRODUCT_NOT_FOUND);
//        }
//
//        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
//                () -> new DomainException(ErrorCode.CART_NOT_FOUND));
//
//        cart.addItem(request.productId(), request.quantity(), marketPolicy);
//    }
}
