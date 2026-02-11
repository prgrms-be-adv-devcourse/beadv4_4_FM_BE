package com.mossy.boundedContext.cash.in;

import com.mossy.boundedContext.cash.app.CashFacade;
import com.mossy.boundedContext.cash.in.dto.request.SellerBalanceRequestDto;
import com.mossy.boundedContext.cash.in.dto.request.UserBalanceRequestDto;
import com.mossy.boundedContext.cash.in.dto.response.SellerWalletResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserCashLogResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserWalletResponseDto;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @PostMapping("/user/{userId}/credit")
    public RsData<Void> creditUserBalance(
            @PathVariable("userId") Long userId,
            @RequestBody UserBalanceRequestDto request) {
        cashFacade.creditUserBalance(request.withUserId(userId));
        return RsData.success(SuccessCode.CASH_CREDIT_SUCCESS);
    }

    @Operation(summary = "구매자 예치금 차감", description = "주문 결제 등의 사유로 구매자의 예치금을 차감합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차감 성공"),
            @ApiResponse(responseCode = "400", description = "잔액 부족"),
            @ApiResponse(responseCode = "404", description = "지갑을 찾을 수 없음")
    })
    @PostMapping("/user/{userId}/deduct")
    public RsData<Void> deductUserBalance(
            @PathVariable("userId") Long userId,
            @RequestBody UserBalanceRequestDto request) {
        cashFacade.deductUserBalance(request.withUserId(userId));
        return RsData.success(SuccessCode.CASH_DEDUCT_SUCCESS);
    }

    @Operation(summary = "구매자 지갑 상세 조회", description = "지갑 ID, 현재 잔액, 사용자 정보를 포함한 상세 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/user/{userId}")
    public RsData<UserWalletResponseDto> getUserWallet(
            @PathVariable("userId") Long userId) {
        UserWalletResponseDto wallet = cashFacade.findUserWallet(userId);
        return RsData.success(SuccessCode.USER_WALLET_FOUND, wallet);
    }

    @Operation(summary = "구매자 잔액 단건 조회", description = "결제 가능 여부 확인을 위해 현재 잔액(BigDecimal)만 신속하게 조회합니다.")
    @GetMapping("/user/{userId}/balance")
    public RsData<BigDecimal> getUserBalance(
            @PathVariable("userId") Long userId) {
        BigDecimal balance = cashFacade.findUserBalance(userId);
        return RsData.success(SuccessCode.USER_BALANCE_FOUND, balance);
    }

    @Operation(summary = "구매자 캐시 사용 내역 조회", description = "현재 로그인한 사용자의 모든 캐시 충전, 사용, 환불 이력(UserCashLog)을 리스트로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내역 조회 성공")
    @GetMapping("/user/{userId}/wallet/logs")
    public RsData<List<UserCashLogResponseDto>> getUserCashLogs(
            @PathVariable("userId") Long userId) {
        List<UserCashLogResponseDto> logs = cashFacade.findAllCashLogs(userId);
        return RsData.success(SuccessCode.USER_CASH_LOGS_FOUND, logs);
    }

    // --- [판매자(Seller) 관련 API] ---

    @Operation(summary = "판매자 대금 입금", description = "판매 수익이나 정산 예정 금액을 판매자 지갑에 입금 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 성공")
    })
    @PostMapping("/seller/{sellerId}/credit")
    public RsData<Void> creditSellerBalance(
            @PathVariable("sellerId") Long sellerId,
            @RequestBody SellerBalanceRequestDto request) {
        cashFacade.creditSellerBalance(request.withSellerId(sellerId));
        return RsData.success(SuccessCode.SELLER_CREDIT_SUCCESS);
    }

    @Operation(summary = "판매자 정산금 출금", description = "판매자의 정산 신청 시 지갑에서 해당 금액만큼 차감합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출금 처리 성공"),
            @ApiResponse(responseCode = "400", description = "정산 가능 금액 초과")
    })
    @PostMapping("/seller/{sellerId}/deduct")
    public RsData<Void> deductSellerBalance(
            @PathVariable("sellerId") Long sellerId,
            @RequestBody SellerBalanceRequestDto request) {
        cashFacade.deductSellerBalance(request.withSellerId(sellerId));
        return RsData.success(SuccessCode.SELLER_DEDUCT_SUCCESS);
    }

    @Operation(summary = "판매자 정산 지갑 상세 조회", description = "판매자 지갑 정보와 정산에 필요한 레플리카 정보를 상세 조회합니다.")
    @GetMapping("/seller/{sellerId}")
    public RsData<SellerWalletResponseDto> getSellerWallet(
            @PathVariable("sellerId") Long sellerId) {
        SellerWalletResponseDto wallet = cashFacade.findSellerWallet(sellerId);
        return RsData.success(SuccessCode.SELLER_WALLET_FOUND, wallet);
    }

    @Operation(summary = "판매자 정산 가능 잔액 조회", description = "판매자 지갑의 현재 출금 가능 잔액만을 조회합니다.")
    @GetMapping("/seller/{sellerId}/balance")
    public RsData<BigDecimal> getSellerBalance(
            @PathVariable("sellerId") Long sellerId) {
        BigDecimal balance = cashFacade.findSellerBalance(sellerId);
        return RsData.success(SuccessCode.SELLER_BALANCE_FOUND, balance);
    }
}