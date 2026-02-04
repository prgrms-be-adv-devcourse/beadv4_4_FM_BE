package com.mossy.member.domain.market;

import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;

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
