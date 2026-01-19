package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.ProductImage;
import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.boundedContext.market.domain.ProductStatus;

import java.math.BigDecimal;
import java.util.List;


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
        List<String> imageUrls
) {
    public static ProductResponse from(Product product) {
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
                product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .toList()
        );
    }
}