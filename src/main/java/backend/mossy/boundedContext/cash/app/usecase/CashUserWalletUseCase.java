package backend.mossy.boundedContext.cash.app.usecase;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.wallet.CashUser;
import backend.mossy.boundedContext.cash.domain.wallet.Wallet;
import backend.mossy.boundedContext.cash.out.CashUserRepository;
import backend.mossy.boundedContext.cash.out.WalletRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.shared.cash.dto.common.CashUserDto;
import backend.mossy.shared.cash.dto.response.WalletResponseDto;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashUserWalletUseCase {

    private final CashSupport cashSupport;
    private final CashUserRepository cashUserRepository;
    private final WalletRepository walletRepository;

    public Wallet createWallet(CashUserDto userDto) {
        cashSupport.validateWalletExists(userDto.id());

        CashUser user = cashUserRepository.getReferenceById(userDto.id());
        Wallet wallet = new Wallet(user);
        return walletRepository.save(wallet);
    }

    public WalletResponseDto getMyWallet(Long userId) {
        return walletRepository.findWalletByUserId(userId)
            .map(WalletResponseDto::from)
            .orElseThrow(
                () -> new DomainException("NOT_FOUND_WALLET", "지갑 정보를 찾을 수 없습니다." + userId));
    }

    public BigDecimal getBalance(Long userId) {
        return walletRepository.findWalletByUserId(userId)
            .map(Wallet::getBalance)
            .orElse(BigDecimal.ZERO);
    }
}
