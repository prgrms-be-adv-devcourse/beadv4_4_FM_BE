package com.mossy.boundedContext.cash.app.usecase.user;

import com.mossy.boundedContext.cash.app.CashSupport;
import com.mossy.boundedContext.cash.domain.user.CashUser;
import com.mossy.boundedContext.cash.domain.user.UserWallet;
import com.mossy.boundedContext.cash.out.user.CashUserRepository;
import com.mossy.boundedContext.cash.out.user.UserWalletRepository;
import com.mossy.shared.member.payload.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashCreateUserWalletUseCase {

    private final CashSupport cashSupport;
    private final CashUserRepository cashUserRepository;
    private final UserWalletRepository userWalletRepository;

    @Transactional
    public void createUserWallet(UserPayload userPayload) {
        cashSupport.validateUserWalletExists(userPayload.id());

        CashUser user = cashUserRepository.getReferenceById(userPayload.id());
        UserWallet wallet = new UserWallet(user);
        userWalletRepository.save(wallet);
    }
}