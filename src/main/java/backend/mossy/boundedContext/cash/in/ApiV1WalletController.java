package backend.mossy.boundedContext.cash.in;

import backend.mossy.boundedContext.cash.app.CashFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.cash.dto.request.SellerBalanceRequestDto;
import backend.mossy.shared.cash.dto.request.UserBalanceRequestDto;
import backend.mossy.shared.cash.dto.response.SellerWalletResponseDto;
import backend.mossy.shared.cash.dto.response.UserWalletResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cash/wallets")
@RequiredArgsConstructor
public class ApiV1WalletController {

    private final CashFacade cashFacade;

    // --- [구매자(User) 관련 API] ---

    @PostMapping("/users/{userId}/credit")
    @Operation(summary = "구매자 예치금 충전", description = "구매자의 지갑에 특정 금액을 충전하고 이력을 남깁니다.")
    public RsData<Void> creditUserBalance(@PathVariable("userId") Long userId, @RequestBody UserBalanceRequestDto request) {
        cashFacade.creditUserBalance(request.withUserId(userId));
        return new RsData<>("C-200", "예치금이 성공적으로 충전되었습니다.");
    }

    @PostMapping("/users/{userId}/deduct")
    @Operation(summary = "구매자 예치금 차감", description = "결제 등의 사유로 구매자의 예치금을 차감합니다.")
    public RsData<Void> deductUserBalance(@PathVariable("userId") Long userId, @RequestBody UserBalanceRequestDto request) {
        cashFacade.deductUserBalance(request.withUserId(userId));
        return new RsData<>("C-200", "예치금 차감이 완료되었습니다.");
    }

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

    // --- [판매자(Seller) 관련 API] ---

    @PostMapping("/sellers/{sellerId}/credit")
    @Operation(summary = "판매자 대금 입금", description = "판매 수익 또는 정산 대금을 판매자 지갑에 입금합니다.")
    public RsData<Void> creditSellerBalance(@PathVariable("sellerId") Long sellerId, @RequestBody SellerBalanceRequestDto request) {
        cashFacade.creditSellerBalance(request.withSellerId(sellerId));
        return new RsData<>("C-200", "판매 대금 입금이 완료되었습니다.");
    }

    @PostMapping("/sellers/{sellerId}/deduct")
    @Operation(summary = "판매자 정산금 출금", description = "판매자가 정산 신청을 하여 지갑에서 금액을 차감합니다.")
    public RsData<Void> deductSellerBalance(@PathVariable("sellerId") Long sellerId, @RequestBody SellerBalanceRequestDto request) {
        cashFacade.deductSellerBalance(request.withSellerId(sellerId));
        return new RsData<>("C-200", "정산용 잔액 차감이 완료되었습니다.");
    }

    @GetMapping("/sellers/{sellerId}")
    @Operation(
        summary = "판매자 정산 지갑 상세 조회",
        description = "판매자의 정산용 지갑 정보와 판매자 레플리카 정보를 포함한 상세 내역을 조회합니다."
    )
    public RsData<SellerWalletResponseDto> getSellerWallet(@PathVariable("sellerId") Long sellerId) {
        SellerWalletResponseDto wallet = cashFacade.findSellerWallet(sellerId);
        return new RsData<>("C-200", "판매자(%d)님의 지갑 정보가 정상적으로 조회되었습니다.".formatted(sellerId), wallet);
    }

    @GetMapping("/sellers/{sellerId}/balance")
    @Operation(
        summary = "판매자 정산 가능 잔액 조회",
        description = "정산 도메인에서 실제 출금 가능 금액을 파악하기 위해 판매자 지갑의 잔액만을 조회합니다."
    )
    public RsData<BigDecimal> getSellerBalance(@PathVariable("sellerId") Long sellerId) {
        BigDecimal balance = cashFacade.findSellerBalance(sellerId);
        return new RsData<>("C-200", "판매자 잔액 조회 성공", balance);
    }
}