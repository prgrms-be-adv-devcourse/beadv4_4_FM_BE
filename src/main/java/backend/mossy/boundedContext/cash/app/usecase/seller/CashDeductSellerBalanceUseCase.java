package backend.mossy.boundedContext.cash.app.usecase.seller;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import backend.mossy.shared.cash.dto.request.SellerBalanceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashDeductSellerBalanceUseCase {

    private final CashSupport cashSupport;

    public void deduct(SellerBalanceRequestDto request) {
        SellerWallet wallet = cashSupport.findWalletBySellerId(request.sellerId());

        wallet.debit(request.amount(), request.eventType(), request.relTypeCode(), request.relId());
    }
}
