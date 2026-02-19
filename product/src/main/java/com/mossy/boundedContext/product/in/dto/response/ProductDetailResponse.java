package com.mossy.boundedContext.product.in.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        CatalogDto catalog,
        ProductDto mainProduct,
        List<OtherSellerDto> otherSellers
) {
    public record CatalogDto(
            Long id,
            String name,
            String brand,
            String description,
            List<ImageDto> images,
            Long categoryId,
            String categoryName
    ) {}

    public record ImageDto(
            String imageUrl,
            Boolean isThumbnail
    ) {}

    public record ProductDto(
            Long productId, Long sellerId, BigDecimal basePrice,
            List<OptionGroupDto> optionGroups,
            List<ProductItemDto> productItems
    ) {}

    public record OptionGroupDto(
            Long groupId,
            String name,
            List<String> values
    ) {}

    public record ProductItemDto(
            Long productItemsId,
            String optionCombination,
            BigDecimal additionalPrice,
            BigDecimal totalPrice,
            Integer quantity,
            BigDecimal weight,
            String status
    ) {}

    public record OtherSellerDto(
            Long productId,
            Long sellerId,
            BigDecimal basePrice
    ) {}
}
