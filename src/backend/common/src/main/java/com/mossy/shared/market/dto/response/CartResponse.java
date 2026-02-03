package com.mossy.shared.market.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
        Long cartId,
        String buyerName,
        String buyerAddress,
        int itemCount,
        List<ProductInfoResponse> items
) {
}