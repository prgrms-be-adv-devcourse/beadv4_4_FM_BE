package backend.mossy.boundedContext.cash.app;

import backend.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import backend.mossy.boundedContext.cash.out.user.UserWalletRepository;
import backend.mossy.global.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashSupport {

    private final UserWalletRepository userWalletRepository;
    private final SellerWalletRepository sellerWalletRepository;

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
}
