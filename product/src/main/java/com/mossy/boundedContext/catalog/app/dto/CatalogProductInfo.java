package com.mossy.boundedContext.catalog.app.dto;

import com.mossy.boundedContext.catalog.domain.CatalogProduct;

import java.math.BigDecimal;

public record CatalogProductInfo(
        Long id,
        String name,
        BigDecimal weight
) {
    public static CatalogProductInfo from(CatalogProduct catalog) {
        return new CatalogProductInfo(
                catalog.getId(),
                catalog.getName(),
                catalog.getWeight()
        );
    }
}
