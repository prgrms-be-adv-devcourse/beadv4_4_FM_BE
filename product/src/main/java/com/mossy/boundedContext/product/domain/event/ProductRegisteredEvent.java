package com.mossy.boundedContext.product.domain.event;

public record ProductRegisteredEvent(
        Long catalogProductId,
        Double minPrice,
        Long sellerCount
) {}