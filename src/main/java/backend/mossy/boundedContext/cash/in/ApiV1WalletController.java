package backend.mossy.boundedContext.cash.in;

import backend.mossy.boundedContext.cash.app.CashFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.cash.dto.response.WalletResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cash/wallets")
@RequiredArgsConstructor
public class ApiV1WalletController {

    private final CashFacade cashFacade;

    /**
     * [최종 개선 형태] Spring Security 통합 방식
     * @AuthenticationPrincipal을 통해 세션/토큰에 저장된 유저 정보를 안전하게 주입받을 예정
     */

    @GetMapping("/users/{userId}")
    @Operation(summary = "내 지갑 정보 상세 조회")
    public RsData<WalletResponseDto> getMyWallet(@PathVariable("userId") Long userId) {
        WalletResponseDto wallet = cashFacade.findWalletByUserId(userId);
        return new RsData<>("C-200", "회원(%d)의 지갑 정보가 정상적으로 조회되었습니다.".formatted(userId), wallet);
    }

    @GetMapping("/users/{userId}/balance")
    @Operation(summary = "내 잔액 단건 조회")
    public RsData<BigDecimal> getBalance(@PathVariable("userId") Long userId) {
        BigDecimal balance = cashFacade.findBalanceByUserId(userId);
        return new RsData<>("C-200", "잔액 조회 성공", balance);
    }
}
