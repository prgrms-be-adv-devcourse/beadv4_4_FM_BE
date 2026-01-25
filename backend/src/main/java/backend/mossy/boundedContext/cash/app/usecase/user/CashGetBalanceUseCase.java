package backend.mossy.boundedContext.cash.app.usecase.user;

import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.boundedContext.cash.out.user.UserWalletRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashGetBalanceUseCase {
    private final UserWalletRepository userWalletRepository;

    public BigDecimal getUserWalletBalance(Long userId) {
        return userWalletRepository.findWalletByUserId(userId)
            .map(UserWallet::getBalance)
            .orElse(BigDecimal.ZERO);
    }
}