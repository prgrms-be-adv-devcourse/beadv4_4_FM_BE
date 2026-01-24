package backend.mossy.boundedContext.cash.in;

import backend.mossy.boundedContext.auth.infra.security.UserDetailsImpl;
import backend.mossy.boundedContext.cash.app.CashFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.cash.dto.request.SellerBalanceRequestDto;
import backend.mossy.shared.cash.dto.request.UserBalanceRequestDto;
import backend.mossy.shared.cash.dto.response.SellerWalletResponseDto;
import backend.mossy.shared.cash.dto.response.UserWalletResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Wallet", description = "예치금 및 지갑 관리 API")
@RestController
@RequestMapping("/api/v1/cash/wallets")
@RequiredArgsConstructor
public class ApiV1WalletController {

    private final CashFacade cashFacade;

    // --- [구매자(User) 관련 API] ---

    @Operation(summary = "구매자 예치금 충전", description = "구매자의 지갑에 특정 금액을 충전하고 로그를 기록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "충전 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    @PostMapping("/user/credit")
    public RsData<Void> creditUserBalance(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UserBalanceRequestDto request) {
        cashFacade.creditUserBalance(request.withUserId(userDetails.getUserId()));
        return new RsData<>("C-200", "예치금이 성공적으로 충전되었습니다.");
    }

    @Operation(summary = "구매자 예치금 차감", description = "주문 결제 등의 사유로 구매자의 예치금을 차감합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "차감 성공"),
        @ApiResponse(responseCode = "400", description = "잔액 부족"),
        @ApiResponse(responseCode = "404", description = "지갑을 찾을 수 없음")
    })
    @PostMapping("/user/deduct")
    public RsData<Void> deductUserBalance(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UserBalanceRequestDto request) {
        cashFacade.deductUserBalance(request.withUserId(userDetails.getUserId()));
        return new RsData<>("C-200", "예치금 차감이 완료되었습니다.");
    }

    @Operation(summary = "구매자 지갑 상세 조회", description = "지갑 ID, 현재 잔액, 사용자 정보를 포함한 상세 내역을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/user")
    public RsData<UserWalletResponseDto> getUserWallet(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        UserWalletResponseDto wallet = cashFacade.findUserWallet(userId);
        return new RsData<>("C-200", "회원(%d)의 지갑 정보가 정상적으로 조회되었습니다.".formatted(userId), wallet);
    }

    @Operation(summary = "구매자 잔액 단건 조회", description = "결제 가능 여부 확인을 위해 현재 잔액(BigDecimal)만 신속하게 조회합니다.")
    @GetMapping("/usersbalance")
    public RsData<BigDecimal> getUserBalance(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        BigDecimal balance = cashFacade.findUserBalance(userId);
        return new RsData<>("C-200", "구매자 잔액 조회 성공", balance);
    }

    // --- [판매자(Seller) 관련 API] ---

    @Operation(summary = "판매자 대금 입금", description = "판매 수익이나 정산 예정 금액을 판매자 지갑에 입금 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "입금 성공")
    })
    @PostMapping("/seller/credit")
    public RsData<Void> creditSellerBalance(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SellerBalanceRequestDto request) {
        Long sellerId = userDetails.getSellerId();
        cashFacade.creditSellerBalance(request.withSellerId(sellerId));
        return new RsData<>("C-200", "판매 대금 입금이 완료되었습니다.");
    }

    @Operation(summary = "판매자 정산금 출금", description = "판매자의 정산 신청 시 지갑에서 해당 금액만큼 차감합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "출금 처리 성공"),
        @ApiResponse(responseCode = "400", description = "정산 가능 금액 초과")
    })
    @PostMapping("/seller/deduct")
    public RsData<Void> deductSellerBalance(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SellerBalanceRequestDto request) {
        Long sellerId = userDetails.getSellerId();
        cashFacade.deductSellerBalance(request.withSellerId(sellerId));
        return new RsData<>("C-200", "정산용 잔액 차감이 완료되었습니다.");
    }

    @Operation(summary = "판매자 정산 지갑 상세 조회", description = "판매자 지갑 정보와 정산에 필요한 레플리카 정보를 상세 조회합니다.")
    @GetMapping("/seller")
    public RsData<SellerWalletResponseDto> getSellerWallet(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long sellerId = userDetails.getSellerId();
        SellerWalletResponseDto wallet = cashFacade.findSellerWallet(sellerId);
        return new RsData<>("C-200", "판매자(%d)님의 지갑 정보가 정상적으로 조회되었습니다.".formatted(sellerId), wallet);
    }

    @Operation(summary = "판매자 정산 가능 잔액 조회", description = "판매자 지갑의 현재 출금 가능 잔액만을 조회합니다.")
    @GetMapping("/seller/balance")
    public RsData<BigDecimal> getSellerBalance(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long sellerId = userDetails.getSellerId();
        BigDecimal balance = cashFacade.findSellerBalance(sellerId);
        return new RsData<>("C-200", "판매자 잔액 조회 성공", balance);
    }
}