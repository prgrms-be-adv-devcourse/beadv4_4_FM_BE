package com.mossy.boundedContext.catalog.app.dto;

public record CatalogReviewInfoDto (
        String name,
        String thumbnail,
        Long reviewCount,
        Double averageRating,
        String categoryName
) { }
