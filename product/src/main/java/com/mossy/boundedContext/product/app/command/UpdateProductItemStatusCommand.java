package com.mossy.boundedContext.product.app.command;

import com.mossy.shared.product.enums.ProductItemStatus;

public record UpdateProductItemStatusCommand(
        Long sellerId,
        Long productId,
        Long productItemId,
        ProductItemStatus status
) {}