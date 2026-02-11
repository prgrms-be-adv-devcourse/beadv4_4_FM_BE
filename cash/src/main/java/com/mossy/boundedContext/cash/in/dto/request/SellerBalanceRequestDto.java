package com.mossy.boundedContext.cash.in.dto.request;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.cash.enums.SellerEventType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SellerBalanceRequestDto(
    Long sellerId,
    BigDecimal amount,
    SellerEventType eventType,
    String relTypeCode,
    Long relId
) {
    public SellerBalanceRequestDto withSellerId(Long sellerId) {
        return SellerBalanceRequestDto.builder()
            .sellerId(sellerId)
            .amount(amount)
            .eventType(eventType)
            .relTypeCode(relTypeCode)
            .relId(relId)
            .build();
    }

    public SellerBalanceRequestDto {
        if (sellerId == null) throw new DomainException(ErrorCode.SELLER_ID_REQUIRED);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCode.AMOUNT_MUST_BE_POSITIVE);
        }
    }
}