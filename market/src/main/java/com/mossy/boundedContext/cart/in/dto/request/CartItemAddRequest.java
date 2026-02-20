package com.mossy.boundedContext.cart.in.dto.request;

public record CartItemAddRequest(
        Long productItemId,
        int quantity
) {
}