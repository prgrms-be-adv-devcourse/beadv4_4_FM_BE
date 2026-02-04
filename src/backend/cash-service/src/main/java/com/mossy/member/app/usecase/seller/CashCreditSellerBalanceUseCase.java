package com.mossy.member.app.usecase.seller;

import com.mossy.member.app.CashSupport;
import com.mossy.member.domain.seller.SellerWallet;
import com.mossy.shared.cash.dto.request.SellerBalanceRequestDto;
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
