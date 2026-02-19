package com.mossy.boundedContext.product.domain.event;

public record ProductPriceChangedEvent(
        Long catalogProductId,
        Double minPrice,
        Long sellerCount
) {}
