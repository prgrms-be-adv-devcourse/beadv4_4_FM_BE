package com.mossy.boundedContext.product.in.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        @NotNull Long sellerId,
        @NotNull Long catalogProductId,
        @NotNull @Positive BigDecimal basePrice,
        @NotEmpty @Valid List<OptionGroupRequest> optionGroups,
        @NotEmpty @Valid List<ProductItemRequest> productItems
) {
    public record OptionGroupRequest(
            @NotNull Long masterId,
            @NotBlank String name
    ) {}

    public record ProductItemRequest(
            @NotNull @Min(0) BigDecimal additionalPrice,
            @NotNull @Min(0) Integer quantity,
            @NotNull @Min(0) BigDecimal weight,
            @NotEmpty @Valid List<ItemOptionMappingRequest> itemOptions
    ) {}

    public record ItemOptionMappingRequest(
            @NotNull Long masterId,
            @NotBlank String value
    ) implements OptionValueRequest {}
}