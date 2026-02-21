package com.mossy.boundedContext.product.in.rest.dto.request;

import com.mossy.shared.product.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record ProductStatusUpdateRequest(
        @NotNull(message = "변경할 상태값은 필수입니다.")
        ProductStatus status
) {
}
