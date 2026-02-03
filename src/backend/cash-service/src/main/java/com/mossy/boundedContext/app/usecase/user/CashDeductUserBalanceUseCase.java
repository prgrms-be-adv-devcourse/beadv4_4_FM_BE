package com.mossy.boundedContext.app.usecase.user;

import com.mossy.boundedContext.app.CashSupport;
import com.mossy.boundedContext.domain.user.UserWallet;
import com.mossy.shared.cash.dto.request.UserBalanceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashDeductUserBalanceUseCase {

    private final CashSupport cashSupport;

    public void deduct(UserBalanceRequestDto request) {
        UserWallet wallet = cashSupport.findWalletByUserId(request.userId());

        wallet.debit(request.amount(), request.eventType(), request.relTypeCode(), request.relId());
    }
}