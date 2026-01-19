package backend.mossy.boundedContext.cash.app.usecase;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.user.CashUser;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.boundedContext.cash.out.CashUserRepository;
import backend.mossy.boundedContext.cash.out.UserWalletRepository;
import backend.mossy.shared.cash.dto.common.CashUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashCreateWalletUseCase {

    private final CashSupport cashSupport;
    private final CashUserRepository cashUserRepository;
    private final UserWalletRepository userWalletRepository;

    public UserWallet createWallet(CashUserDto userDto) {
        cashSupport.validateWalletExists(userDto.id());

        CashUser user = cashUserRepository.getReferenceById(userDto.id());
        UserWallet wallet = new UserWallet(user);
        return userWalletRepository.save(wallet);
    }
}
