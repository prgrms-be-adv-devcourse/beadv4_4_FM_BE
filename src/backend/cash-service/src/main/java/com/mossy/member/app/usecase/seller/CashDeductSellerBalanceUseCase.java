package com.mossy.member.app.usecase.seller;

import com.mossy.member.app.CashSupport;
import com.mossy.member.domain.seller.SellerWallet;
import com.mossy.shared.cash.dto.request.SellerBalanceRequestDto;
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
