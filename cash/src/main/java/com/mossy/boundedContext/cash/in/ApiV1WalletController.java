package com.mossy.boundedContext.cash.in;

import com.mossy.boundedContext.cash.app.CashFacade;
import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.boundedContext.cash.in.dto.request.SellerBalanceRequest;
import com.mossy.boundedContext.cash.in.dto.request.UserBalanceRequestDto;
import com.mossy.boundedContext.cash.in.dto.response.SellerWalletResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.SellerCashLogResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserCashLogResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserWalletResponseDto;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Wallet", description = "예치금 및 지갑 관리 API")
@RestController
@RequestMapping("/api/v1/cash/wallets")
@RequiredArgsConstructor
public class ApiV1WalletController {

    private final CashFacade cashFacade;
    private final CashMapper mapper;

    // --- [구매자(User) 관련 API] ---

    @Operation(summary = "구매자 예치금 충전", description = "구매자의 지갑에 특정 금액을 충전하고 로그를 기록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "충전 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    @PostMapping("/user/credit")
    public RsData<Void> creditUserBalance(
        @RequestHeader("X-User-Id") Long userId,
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
    @PostMapping("/user/deduct")
    public RsData<Void> deductUserBalance(
        @RequestHeader("X-User-Id") Long userId,
        @RequestBody UserBalanceRequestDto request) {
        cashFacade.deductUserBalance(request.withUserId(userId));
        return RsData.success(SuccessCode.CASH_DEDUCT_SUCCESS);
    }

    @Operation(summary = "구매자 지갑 상세 조회", description = "지갑 ID, 현재 잔액, 사용자 정보를 포함한 상세 내역을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/user")
    public RsData<UserWalletResponseDto> getUserWallet(
        @RequestHeader("X-User-Id") Long userId) {
        UserWalletResponseDto wallet = cashFacade.findUserWallet(userId);
        return RsData.success(SuccessCode.USER_WALLET_FOUND, wallet);
    }

    @Operation(summary = "구매자 잔액 단건 조회", description = "결제 가능 여부 확인을 위해 현재 잔액(BigDecimal)만 신속하게 조회합니다.")
    @GetMapping("/user/balance")
    public RsData<BigDecimal> getUserBalance(
        @RequestHeader("X-User-Id") Long userId) {
        BigDecimal balance = cashFacade.findUserBalance(userId);
        return RsData.success(SuccessCode.USER_BALANCE_FOUND, balance);
    }

    @Operation(summary = "구매자 캐시 사용 내역 조회", description = "현재 로그인한 사용자의 모든 캐시 충전, 사용, 환불 이력(UserCashLog)을 리스트로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내역 조회 성공")
    @GetMapping("/user/wallet/logs")
    public RsData<Page<UserCashLogResponseDto>> getUserCashLogs(
        @RequestHeader("X-User-Id") Long userId,
        @PageableDefault(size = 10) Pageable pageable) {
        Page<UserCashLogResponseDto> logs = cashFacade.findAllUserCashLogs(userId, pageable);
        return RsData.success(SuccessCode.USER_CASH_LOGS_FOUND, logs);
    }

    // --- [판매자(Seller) 관련 API] ---

    @Operation(summary = "판매자 대금 입금", description = "판매 수익이나 정산 예정 금액을 판매자 지갑에 입금 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "입금 성공")
    })
    @PostMapping("/seller/credit")
    public RsData<Void> creditSellerBalance(
        @RequestHeader("X-Seller-Id") Long sellerId,
        @RequestBody SellerBalanceRequest request) {
        cashFacade.creditSellerBalance(mapper.toSellerBalanceRequestDto(sellerId, request));
        return RsData.success(SuccessCode.SELLER_CREDIT_SUCCESS);
    }

    @Operation(summary = "판매자 정산금 출금", description = "판매자의 정산 신청 시 지갑에서 해당 금액만큼 차감합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "출금 처리 성공"),
        @ApiResponse(responseCode = "400", description = "정산 가능 금액 초과")
    })
    @PostMapping("/seller/deduct")
    public RsData<Void> deductSellerBalance(
        @RequestHeader("X-Seller-Id") Long sellerId,
        @RequestBody SellerBalanceRequest request) {
        cashFacade.deductSellerBalance(mapper.toSellerBalanceRequestDto(sellerId, request));
        return RsData.success(SuccessCode.SELLER_DEDUCT_SUCCESS);
    }

    @Operation(summary = "판매자 정산 지갑 상세 조회", description = "판매자 지갑 정보와 정산에 필요한 레플리카 정보를 상세 조회합니다.")
    @GetMapping("/seller")
    public RsData<SellerWalletResponseDto> getSellerWallet(
        @RequestHeader("X-Seller-Id") Long sellerId) {
        SellerWalletResponseDto wallet = cashFacade.findSellerWallet(sellerId);
        return RsData.success(SuccessCode.SELLER_WALLET_FOUND, wallet);
    }

    @Operation(summary = "판매자 정산 가능 잔액 조회", description = "판매자 지갑의 현재 출금 가능 잔액만을 조회합니다.")
    @GetMapping("/seller/balance")
    public RsData<BigDecimal> getSellerBalance(
        @RequestHeader("X-Seller-Id") Long sellerId) {
        BigDecimal balance = cashFacade.findSellerBalance(sellerId);
        return RsData.success(SuccessCode.SELLER_BALANCE_FOUND, balance);
    }

    @Operation(summary = "판매자 캐시 사용 내역 조회", description = "판매자의 모든 입금, 출금, 수수료 등 캐시 변동 이력을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내역 조회 성공")
    @GetMapping("/seller/wallet/logs")
    public RsData<Page<SellerCashLogResponseDto>> getSellerCashLogs(
        @RequestHeader("X-Seller-Id") Long sellerId,
        @PageableDefault(size = 10) Pageable pageable) {
        Page<SellerCashLogResponseDto> logs = cashFacade.findAllSellerCashLogs(sellerId, pageable);
        return RsData.success(SuccessCode.SELLER_CASH_LOGS_FOUND, logs);
    }
}