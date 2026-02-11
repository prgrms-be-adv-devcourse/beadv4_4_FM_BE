package com.mossy.boundedContext.product.in.dto.request;

import java.math.BigDecimal;

public record ProductItemRequest(
        String skuCode,
        String optionCombination,
        Integer quantity,
        BigDecimal additionalPrice
) {}
