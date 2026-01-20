package backend.mossy.shared.cash.dto.response;

import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import backend.mossy.shared.cash.dto.event.CashSellerDto;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record SellerWalletResponseDto(
    Long walletId,
    BigDecimal balance,
    CashSellerDto seller // 판매자 지갑 정보 포함
) {

    public static SellerWalletResponseDto from(SellerWallet wallet) {
        return SellerWalletResponseDto.builder()
            .walletId(wallet.getId())
            .balance(wallet.getBalance())
            .seller(CashSellerDto.from(wallet.getSeller()))
            .build();
    }
}
