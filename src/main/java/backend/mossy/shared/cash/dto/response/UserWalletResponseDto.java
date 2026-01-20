package backend.mossy.shared.cash.dto.response;

import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.shared.cash.dto.event.CashUserDto;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record UserWalletResponseDto(
    Long walletId,
    BigDecimal balance,
    CashUserDto user // 구매자 지갑 정보 포함
) {

    public static UserWalletResponseDto from(UserWallet wallet) {
        return UserWalletResponseDto.builder()
            .walletId(wallet.getId())
            .balance(wallet.getBalance())
            .user(CashUserDto.from(wallet.getUser()))
            .build();
    }
}