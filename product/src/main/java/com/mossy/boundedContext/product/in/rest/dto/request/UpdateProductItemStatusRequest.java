package com.mossy.boundedContext.product.in.rest.dto.request;

import com.mossy.shared.product.enums.ProductItemStatus;

public record UpdateProductItemStatusRequest (
        ProductItemStatus status
){}
