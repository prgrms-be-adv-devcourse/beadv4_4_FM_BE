package com.mossy.member.app.usecase.seller;

import com.mossy.member.app.CashSupport;
import com.mossy.member.domain.seller.CashSeller;
import com.mossy.member.domain.seller.SellerWallet;
import com.mossy.member.out.seller.CashSellerRepository;
import com.mossy.member.out.seller.SellerWalletRepository;
import com.mossy.shared.cash.dto.event.CashSellerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashCreateSellerWalletUseCase {

    private final CashSupport cashSupport;
    private final CashSellerRepository cashSellerRepository;
    private final SellerWalletRepository sellerWalletRepository;

    public SellerWallet createSellerWallet(CashSellerDto sellerDto) {
        cashSupport.validateSellerWalletExists(sellerDto.id());

        CashSeller seller = cashSellerRepository.getReferenceById(sellerDto.id());
        SellerWallet wallet = new SellerWallet(seller);
        return sellerWalletRepository.save(wallet);
    }
}