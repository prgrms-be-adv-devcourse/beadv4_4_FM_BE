package com.mossy.boundedContext.catalog.out;

import java.math.BigDecimal;

public interface CatalogSummaryDto {
    Long getCatalogId();
    BigDecimal getMinPrice();
    Long getSellerCount();
}