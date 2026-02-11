package com.mossy.boundedContext.product.in.dto.request;

import java.math.BigDecimal;
import java.util.List;

public record ProductItemRequest(
        String skuCode,
        String optionCombination, // 예: "빨강/XL"
        BigDecimal additionalPrice,
        Integer quantity,
        List<ItemOptionMappingRequest> itemOptions
) {}
