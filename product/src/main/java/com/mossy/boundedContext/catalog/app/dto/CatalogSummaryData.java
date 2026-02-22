package com.mossy.boundedContext.catalog.app.dto;

import java.math.BigDecimal;

public record CatalogSummaryData (
        Long catalogId,
        BigDecimal minPrice,
        Long sellerCount,
        Long minPriceProductItemId
) {}
