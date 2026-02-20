package com.mossy.shared.product.event;

import com.mossy.shared.product.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdatedEvent(
    Long productId,

    String name,
    String categoryName,
    String description,
    BigDecimal price,
    ProductStatus status,

    List<String> optionGroups
) {
}
