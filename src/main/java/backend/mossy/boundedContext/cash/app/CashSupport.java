package backend.mossy.boundedContext.cash.app;

import backend.mossy.boundedContext.cash.domain.CashPolicy;
import backend.mossy.boundedContext.cash.domain.seller.CashSeller;
import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import backend.mossy.boundedContext.cash.domain.user.CashUser;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.boundedContext.cash.out.seller.CashSellerRepository;
import backend.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import backend.mossy.boundedContext.cash.out.user.CashUserRepository;
import backend.mossy.boundedContext.cash.out.user.UserWalletRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashSupport {

    private final UserWalletRepository userWalletRepository;
    private final SellerWalletRepository sellerWalletRepository;
    private final CashUserRepository cashUserRepository;
    private final CashSellerRepository cashSellerRepository;

    public void validateUserWalletExists(Long userId) {
        if (userWalletRepository.existsWalletByUserId(userId)) {
            throw new DomainException(ErrorCode.WALLET_ALREADY_EXISTS);
        }
    }

    public void validateSellerWalletExists(Long sellerId) {
        if (sellerWalletRepository.existsBySellerId(sellerId)) {
            throw new DomainException(ErrorCode.WALLET_ALREADY_EXISTS);
        }
    }

    public CashUser findCashUserById(Long userId) {
        return cashUserRepository.findCashUserById(userId)
            .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));
    }

    public CashSeller findCashSellerById(Long sellerId) {
        return cashSellerRepository.findCashSellerById(sellerId)
            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_NOT_FOUND));
    }

    public UserWallet findWalletByUserId(Long userId) {
        return userWalletRepository.findWalletByUserId(userId)
            .orElseThrow(() -> new DomainException(ErrorCode.USER_WALLET_NOT_FOUND));
    }

    public SellerWallet findWalletBySellerId(Long sellerId) {
        return sellerWalletRepository.findWalletBySellerId(sellerId)
            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_WALLET_NOT_FOUND));
    }

    public SellerWallet findHoldingWallet() {
        return sellerWalletRepository.findBySellerId(CashPolicy.HOLDING_MEMBER_ID)
            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_WALLET_NOT_FOUND));
    }
}
