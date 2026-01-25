package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.domain.product.ProductImage;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;


@Builder
public record ProductDetailResponse(
        Long productId,
        String name,
        String description,
        BigDecimal price,
        BigDecimal weight,
        Integer quantity,
        String status,
        String categoryName,
        Long sellerId,
        String thumbnail,
        List<String> images
) {
    public static ProductDetailResponse from(Product product) {
        String thumbnail = product.getImages().stream()
                .filter(image -> Boolean.TRUE.equals(image.getIsThumbnail()))
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse("https://team07-mossy-storage.s3.ap-northeast-2.amazonaws.com/product/defult.png");
        return ProductDetailResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .weight(product.getWeight())
                .quantity(product.getQuantity())
                .status(product.getStatus().name()) // Enum 처리 확인
                .categoryName(product.getCategory().getName())
                .sellerId(product.getSeller().getId())
                .thumbnail(thumbnail)
                .images(product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .toList())
                .build();
    }
}
