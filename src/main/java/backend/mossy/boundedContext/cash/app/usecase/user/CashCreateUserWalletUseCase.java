package backend.mossy.boundedContext.cash.app.usecase.user;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.user.CashUser;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.boundedContext.cash.out.user.CashUserRepository;
import backend.mossy.boundedContext.cash.out.user.UserWalletRepository;
import backend.mossy.shared.cash.dto.event.CashUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashCreateUserWalletUseCase {

    private final CashSupport cashSupport;
    private final CashUserRepository cashUserRepository;
    private final UserWalletRepository userWalletRepository;

    public UserWallet createUserWallet(CashUserDto userDto) {
        cashSupport.validateUserWalletExists(userDto.id());

        CashUser user = cashUserRepository.getReferenceById(userDto.id());
        UserWallet wallet = new UserWallet(user);
        return userWalletRepository.save(wallet);
    }
}
