package com.mossy.member.app.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCartItemListUseCase {
//
//    private final CartRepository cartRepository;
//    private final ProductApiClient productApiClient;
//    private final MarketSellerRepository marketSellerRepository;
//
//    public CartResponse getCart(Long userId) {
//        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
//                () -> new DomainException(ErrorCode.CART_NOT_FOUND));
//
//        List<ProductInfoResponse> items = productApiClient.findCartItemsByBuyerId(userId);
//
//        return CartResponse.of(cart, items);
//    }
}
