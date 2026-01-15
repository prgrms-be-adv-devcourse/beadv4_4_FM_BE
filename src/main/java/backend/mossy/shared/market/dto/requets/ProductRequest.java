package backend.mossy.shared.market.dto.requets;

import backend.mossy.boundedContext.market.domain.MarketSeller;
import backend.mossy.boundedContext.market.domain.Product;

import java.math.BigDecimal;
import java.util.List;


public record ProductRequest(
        Long sellerId,
        Long userId,
        Long categoryId,
        String name,
        String description,
        BigDecimal weight,
        BigDecimal price,
        Integer quantity,
        String status,
        List<String> imageUrls
) {
    public Product toEntity(MarketSeller seller) {
        return Product.builder()
                .seller(seller)
                .userId(this.userId)
                .categoryId(this.categoryId)
                .name(this.name)
                .description(this.description)
                .weight(this.weight)
                .price(this.price)
                .quantity(this.quantity)
                .status(this.status)
                .build();
    }
}