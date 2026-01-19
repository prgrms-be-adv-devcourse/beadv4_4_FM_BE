package backend.mossy.shared.market.dto.request;

import backend.mossy.boundedContext.market.domain.Category;
import backend.mossy.boundedContext.market.domain.MarketSeller;
import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.boundedContext.market.domain.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        Long sellerId,
        Long userId,
        Long categoryId,
        String name,
        String description,
        BigDecimal weight,
        BigDecimal price,
        Integer quantity,
        ProductStatus status,
        List<String> imageUrls
) {
    public Product toEntity(MarketSeller seller, Category category) {
        return Product.builder()
                .seller(seller)
                .userId(this.userId)
                .category(category)
                .name(this.name)
                .description(this.description)
                .weight(this.weight)
                .price(this.price)
                .quantity(this.quantity)
                .build();
    }
}