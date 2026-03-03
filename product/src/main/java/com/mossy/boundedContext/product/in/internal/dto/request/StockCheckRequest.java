package com.mossy.boundedContext.product.in.internal.dto.request;

public record StockCheckRequest(
        Long productItemId,
        int quantity
) {}
