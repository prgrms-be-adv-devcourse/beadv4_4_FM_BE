package backend.mossy.boundedContext.cash.app.usecase;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.wallet.CashUser;
import backend.mossy.boundedContext.cash.domain.wallet.Wallet;
import backend.mossy.boundedContext.cash.out.CashUserRepository;
import backend.mossy.boundedContext.cash.out.WalletRepository;
import backend.mossy.shared.cash.dto.common.CashUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashCreateWalletUseCase {

    private final CashSupport cashSupport;
    private final CashUserRepository cashUserRepository;
    private final WalletRepository walletRepository;

    public Wallet createWallet(CashUserDto userDto) {
        cashSupport.validateWalletExists(userDto.id());

        CashUser user = cashUserRepository.getReferenceById(userDto.id());
        Wallet wallet = new Wallet(user);
        return walletRepository.save(wallet);
    }
}
