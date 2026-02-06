package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.CashSupport;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.in.dto.request.SellerBalanceRequestDto;
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
