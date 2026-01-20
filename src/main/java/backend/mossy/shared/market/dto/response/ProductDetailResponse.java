package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.domain.product.ProductImage;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;


@Builder // 헤더 위에 선언
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
        List<String> imageUrls
) {
    public static ProductDetailResponse from(Product product) {
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
                .imageUrls(product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .toList())
                .build();
    }
}
