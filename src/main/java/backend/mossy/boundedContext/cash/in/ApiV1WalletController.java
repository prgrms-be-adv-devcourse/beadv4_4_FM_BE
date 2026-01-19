package backend.mossy.boundedContext.cash.in;

import backend.mossy.boundedContext.cash.app.CashFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.cash.dto.response.UserWalletResponseDto;
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
     *
     * @AuthenticationPrincipal을 통해 세션/토큰에 저장된 유저 정보를 안전하게 주입받을 예정
     */

    // --- [구매자(User) 관련 API] ---
    @GetMapping("/users/{userId}")
    @Operation(
        summary = "구매자 지갑 상세 조회",
        description = "특정 사용자의 지갑 식별자(ID), 현재 잔액, 사용자 기본 정보를 상세히 조회합니다."
    )
    public RsData<UserWalletResponseDto> getUserWallet(@PathVariable("userId") Long userId) {
        UserWalletResponseDto wallet = cashFacade.findUserWallet(userId);
        return new RsData<>("C-200", "회원(%d)의 지갑 정보가 정상적으로 조회되었습니다.".formatted(userId), wallet);
    }

    @GetMapping("/users/{userId}/balance")
    @Operation(
        summary = "구매자 잔액 단건 조회",
        description = "구매 도메인에서 결제 가능 여부를 판단하기 위해 구매자의 순수 잔액(BigDecimal)만을 빠르게 조회합니다."
    )
    public RsData<BigDecimal> getUserBalance(@PathVariable("userId") Long userId) {
        BigDecimal balance = cashFacade.findUserBalance(userId);
        return new RsData<>("C-200", "구매자 잔액 조액 성공", balance);
    }
}