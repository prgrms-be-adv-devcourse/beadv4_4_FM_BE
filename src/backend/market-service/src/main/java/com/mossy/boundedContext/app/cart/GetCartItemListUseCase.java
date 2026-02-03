package com.mossy.boundedContext.app.cart;

import com.mossy.boundedContext.domain.cart.Cart;
import com.mossy.boundedContext.out.cart.CartRepository;
import com.mossy.boundedContext.out.market.MarketSellerRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.shared.market.dto.response.CartResponse;
import com.mossy.shared.market.dto.response.ProductInfoResponse;
import com.mossy.shared.market.out.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCartItemListUseCase {

    private final CartRepository cartRepository;
    private final ProductApiClient productApiClient;
    private final MarketSellerRepository marketSellerRepository;

    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
                () -> new DomainException(ErrorCode.CART_NOT_FOUND));

        List<ProductInfoResponse> items = productApiClient.findCartItemsByBuyerId(userId);

        return CartResponse.of(cart, items);
    }
}
