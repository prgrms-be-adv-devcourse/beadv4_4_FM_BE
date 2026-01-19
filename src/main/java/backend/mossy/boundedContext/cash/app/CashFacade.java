package backend.mossy.boundedContext.cash.app;

import backend.mossy.boundedContext.cash.app.usecase.CashUserWalletUseCase;
import backend.mossy.boundedContext.cash.app.usecase.CashSyncUserUseCase;
import backend.mossy.boundedContext.cash.domain.wallet.CashUser;
import backend.mossy.boundedContext.cash.domain.wallet.Wallet;
import backend.mossy.shared.cash.dto.common.CashUserDto;
import backend.mossy.shared.cash.dto.response.WalletResponseDto;
import backend.mossy.shared.member.dto.common.UserDto;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashFacade {

    private final CashSyncUserUseCase cashSyncUserUseCase;
    private final CashUserWalletUseCase cashUserWalletUseCase;

    // 구매자 정보 CashUser로 동기화
    @Transactional
    public CashUser syncUser(UserDto userDto) {
        return cashSyncUserUseCase.syncUser(userDto);
    }

    // 구매자 지갑 생성
    @Transactional
    public Wallet createWallet(CashUserDto userDto) {
        return cashUserWalletUseCase.createWallet(userDto);
    }

    // 지갑 정보 상세 조회
    @Transactional(readOnly = true)
    public WalletResponseDto findWalletByUserId(Long userId) {
        return cashUserWalletUseCase.getMyWallet(userId);
    }

    // 현재 사용 가능한 잔액을 조회
    @Transactional(readOnly = true)
    public BigDecimal findBalanceByUserId(Long userId) {
        return cashUserWalletUseCase.getBalance(userId);
    }
}
