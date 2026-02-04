package com.mossy.member.app.cart;

import com.mossy.member.domain.market.MarketPolicy;
import com.mossy.member.out.cart.CartRepository;
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
