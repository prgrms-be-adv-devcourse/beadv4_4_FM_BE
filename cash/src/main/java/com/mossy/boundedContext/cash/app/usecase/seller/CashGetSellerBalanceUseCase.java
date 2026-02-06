package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashGetSellerBalanceUseCase {

    private final SellerWalletRepository sellerWalletRepository;

    public BigDecimal getSellerBalance(Long sellerId) {
        return sellerWalletRepository.findWalletBySellerId(sellerId)
            .map(SellerWallet::getBalance)
            .orElse(BigDecimal.ZERO);
    }
}
