package com.mossy.boundedContext.order.in.dto.response;

import lombok.Builder;

@Builder
public record OrderDetailSellerResponse(
        String orderNo,
        String buyerName,
        String address,
        String weightGrade,
        int deliveryDistance
) {
}