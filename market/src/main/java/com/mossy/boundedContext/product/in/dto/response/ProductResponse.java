package com.mossy.boundedContext.product.in.dto.response;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductImage;
import com.mossy.shared.market.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductResponse(
        Long productId,
        Long sellerId,
        Long categoryId,
        String name,
        String description,
        BigDecimal weight,
        BigDecimal price,
        Integer quantity,
        ProductStatus status,
        String thumbnail
) {
    public static ProductResponse from(Product product) {
        String thumbnail = product.getImages().stream()
                .filter(image -> Boolean.TRUE.equals(image.getIsThumbnail()))
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse("https://team07-mossy-storage.s3.ap-northeast-2.amazonaws.com/product/defult.png");

        return new ProductResponse(
                product.getId(),
                product.getSeller().getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getDescription(),
                product.getWeight(),
                product.getPrice(),
                product.getQuantity(),
                product.getStatus(),
                thumbnail
        );
    }
}