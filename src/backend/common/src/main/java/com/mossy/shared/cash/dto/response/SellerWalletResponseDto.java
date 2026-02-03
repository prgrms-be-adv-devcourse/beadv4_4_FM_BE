package com.mossy.shared.cash.dto.response;

import com.mossy.shared.cash.dto.event.CashSellerDto;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record SellerWalletResponseDto(
    Long walletId,
    BigDecimal balance,
    CashSellerDto seller // 판매자 지갑 정보 포함
) {
}
