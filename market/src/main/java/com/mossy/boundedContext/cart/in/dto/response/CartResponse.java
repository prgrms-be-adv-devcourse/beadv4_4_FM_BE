package com.mossy.boundedContext.cart.in.dto.response;

import com.mossy.boundedContext.cart.domain.Cart;
import com.mossy.boundedContext.cart.out.external.dto.ProductInfoResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
        Long cartId,
        String buyerAddress,
        int itemCount,
        List<ProductInfoResponse> items
) {

    public static CartResponse of(Cart cart, List<ProductInfoResponse> items) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .buyerAddress(cart.getBuyer().getAddress())
                .itemCount(items.size())
                .items(items)
                .build();
    }
}