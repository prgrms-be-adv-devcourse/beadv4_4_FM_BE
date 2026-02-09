package com.mossy.boundedContext.product.in.dto.request;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.product.domain.Category;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.shared.market.enums.ProductStatus;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        Long sellerId,
        Long categoryId,
        String name,
        String description,
        BigDecimal weight,
        BigDecimal price,
        Integer quantity,
        ProductStatus status,
        List<MultipartFile> images
) {
    public Product toEntity(MarketSeller seller, Category category) {
        return Product.builder()
                .seller(seller)
                .category(category)
                .name(this.name)
                .description(this.description)
                .weight(this.weight)
                .price(this.price)
                .quantity(this.quantity)
                .build();
    }
}