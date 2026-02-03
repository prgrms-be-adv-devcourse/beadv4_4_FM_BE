package com.mossy.boundedContext.app.usecase.user;

import com.mossy.boundedContext.app.CashSupport;
import com.mossy.boundedContext.domain.user.CashUser;
import com.mossy.boundedContext.domain.user.UserWallet;
import com.mossy.boundedContext.out.user.CashUserRepository;
import com.mossy.boundedContext.out.user.UserWalletRepository;
import com.mossy.shared.cash.dto.event.CashUserDto;
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
