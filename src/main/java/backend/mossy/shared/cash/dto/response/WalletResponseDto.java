package backend.mossy.shared.cash.dto.response;

import backend.mossy.boundedContext.cash.domain.wallet.Wallet;
import backend.mossy.shared.cash.dto.common.CashUserDto;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record WalletResponseDto(
    Long walletId,
    BigDecimal balance,
    CashUserDto user // 지갑 소유자 정보 포함
) {

    public static WalletResponseDto from(Wallet wallet) {
        return WalletResponseDto.builder()
            .walletId(wallet.getId())
            .balance(wallet.getBalance())
            .user(CashUserDto.from(wallet.getUser()))
            .build();
    }
}