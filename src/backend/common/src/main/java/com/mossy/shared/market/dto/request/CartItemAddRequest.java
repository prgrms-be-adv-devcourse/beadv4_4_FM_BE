package com.mossy.shared.market.dto.request;

public record CartItemAddRequest(
        Long productId,
        int quantity
) {
}