package com.mossy.boundedContext.marketUser.domain;

import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;

import java.util.UUID;

public record MarketPolicy(
        int maxQuantity
) {
    public void validateCartItemQuantity(int quantity) {
        if (quantity > maxQuantity) {
            throw new DomainException(ErrorCode.QUANTITY_LIMIT_EXCEEDED);
        }
    }

    public String generateOrderNo() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
