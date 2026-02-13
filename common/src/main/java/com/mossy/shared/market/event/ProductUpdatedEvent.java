package com.mossy.shared.market.event;

import com.mossy.shared.market.enums.ProductStatus;
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
