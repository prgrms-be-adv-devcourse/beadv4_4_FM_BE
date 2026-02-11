package com.mossy.boundedContext.cash.app;

import com.mossy.boundedContext.cash.app.usecase.common.CashHoldingUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashCreateSellerWalletUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashCreditSellerBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashDeductSellerBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashGetSellerBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashGetSellerWalletInfoUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashSyncSellerUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashCreateUserWalletUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashCreditUserBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashDeductUserBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashGetBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashGetLogsUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashGetWalletInfoUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashSyncUserUseCase;
import com.mossy.boundedContext.cash.in.dto.command.CashSellerDto;
import com.mossy.boundedContext.cash.in.dto.command.CashUserDto;
import com.mossy.boundedContext.cash.in.dto.request.CashHoldingRequestDto;
import com.mossy.boundedContext.cash.in.dto.request.CashRefundRequestDto;
import com.mossy.boundedContext.cash.in.dto.request.SellerBalanceRequestDto;
import com.mossy.boundedContext.cash.in.dto.request.UserBalanceRequestDto;
import com.mossy.boundedContext.cash.in.dto.response.SellerWalletResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserCashLogResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserWalletResponseDto;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashFacade {

    private final CashSyncUserUseCase cashSyncUserUseCase;
    private final CashSyncSellerUseCase cashSyncSellerUseCase;
    private final CashCreateUserWalletUseCase cashCreateUserWalletUseCase;
    private final CashCreateSellerWalletUseCase cashCreateSellerWalletUseCase;
    private final CashGetWalletInfoUseCase cashGetWalletInfoUseCase;
    private final CashGetBalanceUseCase cashGetBalanceUseCase;
    private final CashGetSellerWalletInfoUseCase cashGetSellerWalletInfoUseCase;
    private final CashGetSellerBalanceUseCase cashGetSellerBalanceUseCase;
    private final CashCreditUserBalanceUseCase cashCreditUserBalanceUseCase;
    private final CashCreditSellerBalanceUseCase cashCreditSellerBalanceUseCase;
    private final CashDeductUserBalanceUseCase cashDeductUserBalanceUseCase;
    private final CashDeductSellerBalanceUseCase cashDeductSellerBalanceUseCase;
    private final CashHoldingUseCase cashHoldingUseCase;
    private final CashGetLogsUseCase cashGetLogsUseCase;

    // === [동기화 영역] ===

    public void syncUser(CashUserDto userDto) {
        cashSyncUserUseCase.syncUser(userDto);
    }

    public void syncSeller(CashSellerDto sellerDto) {
        cashSyncSellerUseCase.syncSeller(sellerDto);
    }

    // === [지갑 영역] ===

    public void createUserWallet(CashUserDto userDto) {
        cashCreateUserWalletUseCase.createUserWallet(userDto);
    }

    public void createSellerWallet(CashSellerDto sellerDto) {
        cashCreateSellerWalletUseCase.createSellerWallet(sellerDto);
    }

    public void creditUserBalance(UserBalanceRequestDto request) {
        cashCreditUserBalanceUseCase.credit(request);
    }

    public void creditSellerBalance(SellerBalanceRequestDto request) {
        cashCreditSellerBalanceUseCase.credit(request);
    }

    public void deductUserBalance(UserBalanceRequestDto request) {
        cashDeductUserBalanceUseCase.deduct(request);
    }

    public void deductSellerBalance(SellerBalanceRequestDto request) {
        cashDeductSellerBalanceUseCase.deduct(request);
    }

    public void cashHolding(CashHoldingRequestDto request) {
        cashHoldingUseCase.holdPaymentAmount(request);
    }

    public void processRefund(CashRefundRequestDto request) {
        cashHoldingUseCase.processRefund(request);
    }

    // === [조회 영역] ===

    public UserWalletResponseDto findUserWallet(Long userId) {
        return cashGetWalletInfoUseCase.getUserWalletInfo(userId);
    }

    public BigDecimal findUserBalance(Long userId) {
        return cashGetBalanceUseCase.getUserWalletBalance(userId);
    }

    public SellerWalletResponseDto findSellerWallet(Long sellerId) {
        return cashGetSellerWalletInfoUseCase.getSellerWalletInfo(sellerId);
    }

    public BigDecimal findSellerBalance(Long sellerId) {
        return cashGetSellerBalanceUseCase.getSellerBalance(sellerId);
    }

    public List<UserCashLogResponseDto> findAllCashLogs(Long userId) {
        return cashGetLogsUseCase.findCashLog(userId);
    }
}