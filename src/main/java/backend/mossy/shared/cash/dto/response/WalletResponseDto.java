package backend.mossy.shared.cash.dto.response;

import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.shared.cash.dto.common.CashUserDto;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record WalletResponseDto(
    Long walletId,
    BigDecimal balance,
    CashUserDto user // 지갑 소유자 정보 포함
) {

    public static WalletResponseDto from(UserWallet wallet) {
        return WalletResponseDto.builder()
            .walletId(wallet.getId())
            .balance(wallet.getBalance())
            .user(CashUserDto.from(wallet.getUser()))
            .build();
    }
}