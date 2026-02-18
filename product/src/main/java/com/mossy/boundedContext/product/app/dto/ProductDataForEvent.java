package com.mossy.boundedContext.product.app.dto;

public record ProductDataForEvent(
        Long productId,
        Long catalogId,
        Double minPrice,
        Long sellerCount
) {}
