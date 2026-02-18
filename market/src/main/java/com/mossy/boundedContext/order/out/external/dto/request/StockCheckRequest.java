package com.mossy.boundedContext.order.out.external.dto.request;

public record StockCheckRequest(
        Long productItemId,
        int quantity
) {
}
