package com.mossy.boundedContext.cart.in.dto.request;

import java.util.List;

public record CartItemDeleteRequest(
        List<Long> productIds
) { }
