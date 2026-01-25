package backend.mossy.boundedContext.cash.app.usecase.seller;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import backend.mossy.shared.cash.dto.request.SellerBalanceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashCreditSellerBalanceUseCase {
    private final CashSupport cashSupport;

    public void credit(SellerBalanceRequestDto request) {
        SellerWallet sellerWallet = cashSupport.findWalletBySellerId(request.sellerId());

        //balance 증가, userCashLog가 자동 추가
        sellerWallet.credit(request.amount(), request.eventType(), request.relTypeCode(), request.relId());
    }
}
