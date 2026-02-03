package com.mossy.shared.cash.dto.response;

import com.mossy.shared.cash.dto.event.CashUserDto;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record UserWalletResponseDto(
    Long walletId,
    BigDecimal balance,
    CashUserDto user // 구매자 지갑 정보 포함
) {
}