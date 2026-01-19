package backend.mossy.boundedContext.cash.app;

import backend.mossy.boundedContext.cash.out.CashUserRepository;
import backend.mossy.boundedContext.cash.out.WalletRepository;
import backend.mossy.global.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashSupport {

    private final WalletRepository walletRepository;
    private final CashUserRepository cashUserRepository;

    public void validateWalletExists(Long userId) {
        if (walletRepository.existsWalletByUserId(userId)) {
            // 추후 GlobalExceptionHandler에서 공통으로 처리할 계획
            throw new DomainException("ALREADY_EXISTS_WALLET", "이미 생성된 지갑이 존재합니다.: " + userId);
        }
    }
}
