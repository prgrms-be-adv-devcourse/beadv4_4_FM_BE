package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.product.ProductImage;
import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.domain.product.ProductStatus;

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