package com.mossy.boundedContext.product.domain.event;

import java.math.BigDecimal;

public record ProductCatalogSyncEvent(
        Long catalogProductId,
        BigDecimal minPrice,
        Long sellerCount,
        Long minPriceProductItemId
) {}
