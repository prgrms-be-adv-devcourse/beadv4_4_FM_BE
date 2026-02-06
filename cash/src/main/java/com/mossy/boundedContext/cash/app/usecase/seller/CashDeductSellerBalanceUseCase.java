package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.CashSupport;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.in.dto.request.SellerBalanceRequestDto;
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
