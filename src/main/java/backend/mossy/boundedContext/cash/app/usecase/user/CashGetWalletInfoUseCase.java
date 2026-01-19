package backend.mossy.boundedContext.cash.app.usecase.user;

import backend.mossy.boundedContext.cash.out.user.UserWalletRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.shared.cash.dto.response.WalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashGetWalletInfoUseCase {

    private final UserWalletRepository userWalletRepository;

    public WalletResponseDto getMyWallet(Long userId) {
        return userWalletRepository.findWalletByUserId(userId)
            .map(WalletResponseDto::from)
            .orElseThrow(
                () -> new DomainException("NOT_FOUND_WALLET", "지갑 정보를 찾을 수 없습니다." + userId));
    }
}
