package com.mossy.boundedContext.cash.in.dto.response;

import com.mossy.boundedContext.cash.in.dto.common.CashUserDto;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record UserWalletResponseDto(
    Long walletId,
    BigDecimal balance,
    CashUserDto user
) {
}