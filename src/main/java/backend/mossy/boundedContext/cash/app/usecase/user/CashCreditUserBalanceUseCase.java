package backend.mossy.boundedContext.cash.app.usecase.user;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.shared.cash.dto.request.UserRechargeBalanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashCreditUserBalanceUseCase {

    private final CashSupport cashSupport;

    public void credit(UserRechargeBalanceRequest request) {
        UserWallet userWallet = cashSupport.findWalletByUserId(request.userId());

        //balance 증가, userCashLog가 자동 추가
        userWallet.credit(request.amount(), request.eventType(), "BANK_RECHARGE",
            request.bankTransactionId());
    }
}
