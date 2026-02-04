package com.mossy.member.app.usecase.user;

import com.mossy.member.app.CashSupport;
import com.mossy.member.domain.user.UserWallet;
import com.mossy.shared.cash.dto.request.UserBalanceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashCreditUserBalanceUseCase {

    private final CashSupport cashSupport;

    public void credit(UserBalanceRequestDto request) {
        UserWallet userWallet = cashSupport.findWalletByUserId(request.userId());

        //balance 증가, userCashLog가 자동 추가
        userWallet.credit(request.amount(), request.eventType(), request.relTypeCode(), request.relId());
    }
}
