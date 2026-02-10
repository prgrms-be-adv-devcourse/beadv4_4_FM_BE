package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.CashSupport;
import com.mossy.boundedContext.cash.domain.seller.CashSeller;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.in.dto.command.CashSellerDto;
import com.mossy.boundedContext.cash.out.seller.CashSellerRepository;
import com.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashCreateSellerWalletUseCase {

    private final CashSupport cashSupport;
    private final CashSellerRepository cashSellerRepository;
    private final SellerWalletRepository sellerWalletRepository;

    @Transactional
    public void createSellerWallet(CashSellerDto sellerDto) {
        cashSupport.validateSellerWalletExists(sellerDto.sellerId());

        CashSeller seller = cashSellerRepository.getReferenceById(sellerDto.sellerId());
        SellerWallet wallet = new SellerWallet(seller);
        sellerWalletRepository.save(wallet);
    }
}