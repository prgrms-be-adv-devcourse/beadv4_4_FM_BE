package com.mossy.boundedContext.cash.in.dto.response;

import com.mossy.boundedContext.cash.in.dto.common.CashSellerDto;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record SellerWalletResponseDto(
    Long walletId,
    BigDecimal balance,
    CashSellerDto seller
) {
}