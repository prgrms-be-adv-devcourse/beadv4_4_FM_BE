package com.mossy.shared.market.event;

import java.util.List;

public record OrderStockReturnEvent(
        List<StockItem> items
) {
    public record StockItem(
            Long productItemId,
            Integer quantity
    ) {}
}
