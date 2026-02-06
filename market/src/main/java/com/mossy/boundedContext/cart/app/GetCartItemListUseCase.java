package com.mossy.boundedContext.cart.app;

import com.mossy.boundedContext.cart.domain.Cart;
import com.mossy.boundedContext.cart.in.dto.response.CartResponse;
import com.mossy.boundedContext.cart.out.CartRepository;
import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.product.in.dto.response.ProductInfoResponse;
import com.mossy.boundedContext.product.out.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCartItemListUseCase {

    private final CartRepository cartRepository;
    private final ProductApiClient productApiClient;

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
                () -> new DomainException(ErrorCode.CART_NOT_FOUND));

        List<ProductInfoResponse> items = productApiClient.findCartItemsByBuyerId(userId);

        return CartResponse.of(cart, items);
    }
}
