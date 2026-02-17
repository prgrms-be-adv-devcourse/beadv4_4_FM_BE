package com.mossy.boundedContext.catalog.app.dto;

import com.mossy.boundedContext.catalog.domain.CatalogProduct;

public record CatalogDto(
        Long id,
        String name,
        String brand,
        String description,
        String thumbnail,
        String categoryName
) {
    public static CatalogDto from(CatalogProduct entity) {
        return new CatalogDto(
                entity.getId(),
                entity.getName(),
                entity.getBrand(),
                entity.getDescription(),
                entity.getThumbnail(),
                entity.getCategory().getName()
        );
    }
}
