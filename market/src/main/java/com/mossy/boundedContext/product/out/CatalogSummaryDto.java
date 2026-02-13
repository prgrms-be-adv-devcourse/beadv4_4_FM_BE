package com.mossy.boundedContext.product.out;

import java.math.BigDecimal;

public interface CatalogSummaryDto {
    Long getCatalogId();
    BigDecimal getMinPrice();
    Long getSellerCount();
}