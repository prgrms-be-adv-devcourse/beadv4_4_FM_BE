package backend.mossy.boundedContext.cash.app;

import backend.mossy.boundedContext.cash.app.usecase.user.CashCreateWalletUseCase;
import backend.mossy.boundedContext.cash.app.usecase.user.CashGetBalanceUseCase;
import backend.mossy.boundedContext.cash.app.usecase.user.CashGetWalletInfoUseCase;
import backend.mossy.boundedContext.cash.app.usecase.sync.CashSyncUserUseCase;
import backend.mossy.boundedContext.cash.domain.user.CashUser;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.shared.cash.dto.common.CashUserDto;
import backend.mossy.shared.cash.dto.response.WalletResponseDto;
import backend.mossy.shared.member.dto.event.UserDto;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashFacade {

    private final CashSyncUserUseCase cashSyncUserUseCase;
    private final CashCreateWalletUseCase cashCreateWalletUseCase;
    private final CashGetWalletInfoUseCase cashGetWalletInfoUseCase;
    private final CashGetBalanceUseCase cashGetBalanceUseCase;

    // 구매자 정보 CashUser로 동기화
    @Transactional
    public CashUser syncUser(UserDto userDto) {
        return cashSyncUserUseCase.syncUser(userDto);
    }

    // 구매자 지갑 생성
    @Transactional
    public UserWallet createWallet(CashUserDto userDto) {
        return cashCreateWalletUseCase.createWallet(userDto);
    }

    // 지갑 정보 상세 조회
    @Transactional(readOnly = true)
    public WalletResponseDto findWalletByUserId(Long userId) {
        return cashGetWalletInfoUseCase.getMyWallet(userId);
    }

    // 현재 사용 가능한 잔액을 조회
    @Transactional(readOnly = true)
    public BigDecimal findBalanceByUserId(Long userId) {
        return cashGetBalanceUseCase.getBalance(userId);
    }
}
