package com.mossy.shared.market.event;

import java.util.List;

public record OrderStockReturnEvent(
        List<OrderItemStock> orderItems
) {
    public record OrderItemStock(
            Long productItemId,
            Integer quantity
    ) {}
}
