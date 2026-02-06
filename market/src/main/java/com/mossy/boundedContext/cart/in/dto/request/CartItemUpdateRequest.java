package com.mossy.boundedContext.cart.in.dto.request;

public record CartItemUpdateRequest(
        Long productId,
        int quantity
) {
}