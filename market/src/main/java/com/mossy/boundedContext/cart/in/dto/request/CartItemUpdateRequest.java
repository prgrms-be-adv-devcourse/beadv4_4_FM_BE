package com.mossy.boundedContext.cart.in.dto.request;

public record CartItemUpdateRequest(
        Long productItemId,
        int quantity
) {
}