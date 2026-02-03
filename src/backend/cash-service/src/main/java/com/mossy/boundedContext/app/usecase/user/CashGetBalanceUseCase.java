package com.mossy.boundedContext.app.usecase.user;

import com.mossy.boundedContext.domain.user.UserWallet;
import com.mossy.boundedContext.out.user.UserWalletRepository;
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