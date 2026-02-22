package com.mossy.boundedContext.product.app.command;

import com.mossy.shared.product.enums.ProductStatus;

public record UpdateProductStatusCommand(
        Long productId,
        Long sellerId,
        ProductStatus newStatus
) {
}
