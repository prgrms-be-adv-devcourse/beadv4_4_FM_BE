package com.mossy.boundedContext.catalog.app.dto;

import com.mossy.boundedContext.catalog.domain.CatalogProduct;

import java.math.BigDecimal;

public record CatalogProductWithCategoryInfo(
        Long id,
        String name,
        BigDecimal weight,
        String categoryName,
        String thumbnailUrl
) {
    public static CatalogProductWithCategoryInfo from(CatalogProduct catalog) {
        return new CatalogProductWithCategoryInfo(
                catalog.getId(),
                catalog.getName(),
                catalog.getWeight(),
                catalog.getCategory() != null ? catalog.getCategory().getName() : "미분류",
                catalog.getThumbnail()
        );
    }
}