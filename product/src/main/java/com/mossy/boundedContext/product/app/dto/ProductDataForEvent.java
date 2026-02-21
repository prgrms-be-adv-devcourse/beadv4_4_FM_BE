package com.mossy.boundedContext.product.app.dto;

import java.math.BigDecimal;

public record ProductDataForEvent(
        Long catalogId,
        BigDecimal minPrice,
        Long sellerCount,
        Long minPriceProductItemId
) {}
