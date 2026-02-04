package com.mossy.member.app.usecase.seller;

import com.mossy.member.domain.seller.SellerWallet;
import com.mossy.member.out.seller.SellerWalletRepository;
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
