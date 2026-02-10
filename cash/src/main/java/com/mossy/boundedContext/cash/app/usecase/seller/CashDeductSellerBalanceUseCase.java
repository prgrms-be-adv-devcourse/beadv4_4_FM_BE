package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.CashSupport;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.in.dto.request.SellerBalanceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashDeductSellerBalanceUseCase {

    private final CashSupport cashSupport;

    @Transactional
    public void deduct(SellerBalanceRequestDto request) {
        SellerWallet wallet = cashSupport.findWalletBySellerId(request.sellerId());

        wallet.debit(request.amount(), request.eventType(), request.relTypeCode(), request.relId());
    }
}
