package com.mossy.boundedContext.cart.app;

import com.mossy.boundedContext.cart.domain.Cart;
import com.mossy.boundedContext.cart.domain.CartItem;
import com.mossy.boundedContext.cart.in.dto.response.CartResponse;
import com.mossy.boundedContext.cart.out.external.CartFeignClient;
import com.mossy.boundedContext.cart.out.CartRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCartItemListUseCase {

    private final CartRepository cartRepository;
    private final CartFeignClient cartFeignClient;

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
                () -> new DomainException(ErrorCode.CART_NOT_FOUND));

        List<Long> productItemIds = cart.getItems().stream()
                .map(CartItem::getProductItemId)
                .toList();

        return CartResponse.of(cart, cartFeignClient.findByIds(productItemIds));
    }
}
