package backend.mossy.boundedContext.cash.app;

import backend.mossy.boundedContext.cash.domain.seller.CashSeller;
import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import backend.mossy.boundedContext.cash.domain.user.CashUser;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.boundedContext.cash.out.seller.CashSellerRepository;
import backend.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import backend.mossy.boundedContext.cash.out.user.CashUserRepository;
import backend.mossy.boundedContext.cash.out.user.UserWalletRepository;
import backend.mossy.global.exception.DomainException;
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
            // 추후 GlobalExceptionHandler에서 공통으로 처리할 계획
            throw new DomainException("ALREADY_EXISTS_WALLET", "이미 생성된 구매자 지갑이 존재합니다.: " + userId);
        }
    }

    public void validateSellerWalletExists(Long sellerId) {
        if (sellerWalletRepository.existsBySellerId(sellerId)) {
            // 추후 GlobalExceptionHandler에서 공통으로 처리할 계획
            throw new DomainException("ALREADY_EXISTS_WALLET", "이미 생성된 판매자 지갑이 존재합니다.: " + sellerId);
        }
    }

    public CashUser findCashUserById(Long userId) {
        return cashUserRepository.findCashUserById(userId)
            .orElseThrow(
                () -> new DomainException("NOT_FOUND_USER", "존재하지 않는 구매자입니다.: " + userId));
    }

    public CashSeller findCashSellerById(Long sellerId) {
        return cashSellerRepository.findCashSellerById(sellerId)
            .orElseThrow(
                () -> new DomainException("NOT_FOUND_SELLER", "존재하지 않는 판매자입니다.: " + sellerId));
    }

    public UserWallet findWalletByUserId(Long userId) {
        return userWalletRepository.findWalletByUserId(userId)
            .orElseThrow(
                () -> new DomainException("NOT_EXISTS_USER_WALLET", "구매자 지갑이 존재하지 않습니다.: " + userId)
            );
    }

    public SellerWallet findWalletBySellerId(Long sellerId) {
        return sellerWalletRepository.findWalletBySellerId(sellerId)
            .orElseThrow(
                () -> new DomainException("NOT_EXISTS_USER_WALLET", "판매자 지갑이 존재하지 않습니다.: " + sellerId)
            );
    }
}
