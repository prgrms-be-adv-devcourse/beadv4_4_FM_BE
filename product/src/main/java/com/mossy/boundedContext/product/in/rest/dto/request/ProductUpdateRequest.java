package com.mossy.boundedContext.product.in.rest.dto.request;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateRequest(
        BigDecimal basePrice,
        List<GroupUpdateRequest> optionGroups,
        List<ItemUpdateRequest> productItems
) {
    public record GroupUpdateRequest(
            Long id,
            String name,
            List<ValueUpdateRequest> itemOptions
    ) {}

    public record ItemUpdateRequest(
            Long id,
            BigDecimal additionalPrice,
            BigDecimal weight,
            Integer quantity,
            List<ValueUpdateRequest> itemOptions
    ) {}

    public record ValueUpdateRequest(
            Long id,
            Long masterId,
            String value
    ) implements OptionValueRequest {}
}