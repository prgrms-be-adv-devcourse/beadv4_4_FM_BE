package com.mossy.boundedContext.cash.in.dto.request;

import com.mossy.shared.cash.enums.SellerEventType;

import java.math.BigDecimal;

public record SellerBalanceRequest(
    BigDecimal amount,
    SellerEventType eventType,
    String relTypeCode,
    Long relId
) {
}