package com.mossy.shared.product.event;

public record ProductClickedEvent(
    Long userId,
    Long productId
) {
}

