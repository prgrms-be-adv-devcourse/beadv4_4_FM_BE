package com.mossy.boundedContext.cash.app.usecase.user;

import com.mossy.boundedContext.cash.domain.user.UserWallet;
import com.mossy.boundedContext.cash.out.user.UserWalletRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashGetBalanceUseCase {
    private final UserWalletRepository userWalletRepository;

    @Transactional(readOnly = true)
    public BigDecimal getUserWalletBalance(Long userId) {
        return userWalletRepository.findWalletByUserId(userId)
            .map(UserWallet::getBalance)
            .orElse(BigDecimal.ZERO);
    }
}