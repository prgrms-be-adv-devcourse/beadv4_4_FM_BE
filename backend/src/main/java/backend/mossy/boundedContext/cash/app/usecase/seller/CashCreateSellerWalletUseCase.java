package backend.mossy.boundedContext.cash.app.usecase.seller;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.seller.CashSeller;
import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import backend.mossy.boundedContext.cash.out.seller.CashSellerRepository;
import backend.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import backend.mossy.shared.cash.dto.event.CashSellerDto;
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