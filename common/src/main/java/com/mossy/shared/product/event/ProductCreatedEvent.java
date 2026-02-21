package com.mossy.shared.product.event;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreatedEvent(
    Long productId,

    String name,
    String categoryName,
    String description,
    BigDecimal price,

    List<String> optionGroups
) {

}
