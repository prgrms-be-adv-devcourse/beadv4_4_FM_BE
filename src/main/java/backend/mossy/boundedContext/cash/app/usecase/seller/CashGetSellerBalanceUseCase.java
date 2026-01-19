package backend.mossy.boundedContext.cash.app.usecase.seller;

import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import backend.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashGetSellerBalanceUseCase {

    private final SellerWalletRepository sellerWalletRepository;

    public BigDecimal getSellerBalance(Long sellerId) {
        return sellerWalletRepository.findBySellerId(sellerId)
            .map(SellerWallet::getBalance)
            .orElse(BigDecimal.ZERO);
    }
}
