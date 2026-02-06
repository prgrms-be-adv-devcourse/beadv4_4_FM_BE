package com.mossy.boundedContext.order.in.dto.request;

import com.mossy.boundedContext.product.in.dto.response.ProductInfoResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderCreatedRequest(
//        String buyerAddress,
//        BigDecimal buyerLatitude,
//        BigDecimal buyerLongitude,
        BigDecimal totalPrice,
        String paymentType,
        List<ProductInfoResponse> items
){ }
