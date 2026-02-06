package com.mossy.boundedContext.cash.app;

import com.mossy.boundedContext.cash.app.mapper.CashPayloadMapper;
import com.mossy.boundedContext.cash.app.usecase.seller.CashCreateSellerWalletUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashCreditSellerBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashDeductSellerBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashGetSellerBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashGetSellerWalletInfoUseCase;
import com.mossy.boundedContext.cash.app.usecase.seller.CashSyncSellerUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashSyncUserUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashCreateUserWalletUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashCreditUserBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashDeductUserBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashGetBalanceUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashGetLogsUseCase;
import com.mossy.boundedContext.cash.app.usecase.user.CashGetWalletInfoUseCase;
import com.mossy.boundedContext.cash.app.usecase.common.CashHoldingUseCase;
import com.mossy.boundedContext.cash.domain.seller.CashSeller;
import com.mossy.boundedContext.cash.domain.user.CashUser;
import com.mossy.boundedContext.cash.domain.user.UserCashLog;
import com.mossy.boundedContext.cash.in.dto.request.SellerBalanceRequestDto;
import com.mossy.boundedContext.cash.in.dto.request.UserBalanceRequestDto;
import com.mossy.boundedContext.cash.in.dto.response.SellerWalletResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserWalletResponseDto;
import com.mossy.shared.market.event.PaymentCompletedEvent;
import com.mossy.shared.market.event.PaymentRefundEvent;
import com.mossy.shared.member.payload.SellerPayload;
import com.mossy.shared.member.payload.UserPayload;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CashUser syncUser(UserPayload userPayload) {
        return cashSyncUserUseCase.syncUser(userPayload);
    }

    @Transactional
    public CashSeller syncSeller(SellerPayload sellerPayload) {
        return cashSyncSellerUseCase.syncSeller(sellerPayload);
    }

    // === [지갑 영역] ===

    @Transactional
    public void createUserWallet(UserPayload userPayload) {
        cashCreateUserWalletUseCase.createUserWallet(userPayload);
    }

    @Transactional
    public void createSellerWallet(SellerPayload sellerPayload) {
        cashCreateSellerWalletUseCase.createSellerWallet(sellerPayload);
    }

    @Transactional
    public void creditUserBalance(UserBalanceRequestDto request) {
        cashCreditUserBalanceUseCase.credit(request);
    }

    @Transactional
    public void creditSellerBalance(SellerBalanceRequestDto request) {
        cashCreditSellerBalanceUseCase.credit(request);
    }

    @Transactional
    public void deductUserBalance(UserBalanceRequestDto request) {
        cashDeductUserBalanceUseCase.deduct(request);
    }

    @Transactional
    public void deductSellerBalance(SellerBalanceRequestDto request) {
        cashDeductSellerBalanceUseCase.deduct(request);
    }

    @Transactional
    public void cashHolding(PaymentCompletedEvent request) {
        cashHoldingUseCase.holdPaymentAmount(request);
    }

    @Transactional
    public void processRefund(PaymentRefundEvent event) {
        cashHoldingUseCase.processRefund(event);
    }

    // === [조회 영역] ===

    @Transactional(readOnly = true)
    public UserWalletResponseDto findUserWallet(Long userId) {
        return cashGetWalletInfoUseCase.getUserWalletInfo(userId);
    }

    @Transactional(readOnly = true)
    public BigDecimal findUserBalance(Long userId) {
        return cashGetBalanceUseCase.getUserWalletBalance(userId);
    }

    @Transactional(readOnly = true)
    public SellerWalletResponseDto findSellerWallet(Long sellerId) {
        return cashGetSellerWalletInfoUseCase.getSellerWalletInfo(sellerId);
    }

    @Transactional(readOnly = true)
    public BigDecimal findSellerBalance(Long sellerId) {
        return cashGetSellerBalanceUseCase.getSellerBalance(sellerId);
    }

    public List<UserCashLog> findAllCashLogs(Long userId) {
        return cashGetLogsUseCase.findCashLog(userId);
    }
}