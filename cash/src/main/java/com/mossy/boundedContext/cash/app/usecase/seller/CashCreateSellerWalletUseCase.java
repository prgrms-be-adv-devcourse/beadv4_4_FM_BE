package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.CashSupport;
import com.mossy.boundedContext.cash.domain.seller.CashSeller;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.out.seller.CashSellerRepository;
import com.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import com.mossy.shared.member.payload.SellerPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashCreateSellerWalletUseCase {

    private final CashSupport cashSupport;
    private final CashSellerRepository cashSellerRepository;
    private final SellerWalletRepository sellerWalletRepository;

    public void createSellerWallet(SellerPayload sellerPayload) {
        cashSupport.validateSellerWalletExists(sellerPayload.sellerId());

        CashSeller seller = cashSellerRepository.getReferenceById(sellerPayload.sellerId());
        SellerWallet wallet = new SellerWallet(seller);
        sellerWalletRepository.save(wallet);
    }
}