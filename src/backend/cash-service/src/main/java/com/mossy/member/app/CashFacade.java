package com.mossy.member.app;

import com.mossy.member.app.usecase.seller.*;
import com.mossy.member.app.usecase.seller.CashGetSellerBalanceUseCase;
import com.mossy.member.app.usecase.seller.CashGetSellerWalletInfoUseCase;
import com.mossy.member.app.usecase.sync.CashSyncSellerUseCase;
import com.mossy.member.app.usecase.sync.CashSyncUserUseCase;
import com.mossy.member.app.usecase.user.CashCreateUserWalletUseCase;
import com.mossy.member.app.usecase.user.CashCreditUserBalanceUseCase;
import com.mossy.member.app.usecase.user.CashDeductUserBalanceUseCase;
import com.mossy.member.app.usecase.user.CashGetBalanceUseCase;
import com.mossy.member.app.usecase.user.CashGetLogsUseCase;
import com.mossy.member.app.usecase.user.CashGetWalletInfoUseCase;
import com.mossy.member.app.usecase.user.CashHoldingUseCase;
import com.mossy.member.domain.seller.CashSeller;
import com.mossy.member.domain.seller.SellerWallet;
import com.mossy.member.domain.user.CashUser;
import com.mossy.member.domain.user.UserCashLog;
import com.mossy.member.domain.user.UserWallet;
import com.mossy.shared.cash.dto.event.CashSellerDto;
import com.mossy.shared.cash.dto.event.CashUserDto;
import com.mossy.shared.cash.dto.request.SellerBalanceRequestDto;
import com.mossy.shared.cash.dto.request.UserBalanceRequestDto;
import com.mossy.shared.member.dto.event.SellerApprovedEvent;
import com.mossy.shared.market.event.PaymentCompletedEvent;
import com.mossy.shared.market.event.PaymentRefundEvent;
import com.mossy.shared.member.dto.event.UserDto;
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
    public CashUser syncUser(UserDto userDto) {
        return cashSyncUserUseCase.syncUser(userDto);
    }

    @Transactional
    public CashSeller syncSeller(SellerApprovedEvent sellerApprovedEvent) {
        return cashSyncSellerUseCase.syncSeller(sellerApprovedEvent);
    }

    // === [지갑 영역] ===

    @Transactional
    public UserWallet createUserWallet(CashUserDto userDto) {
        return cashCreateUserWalletUseCase.createUserWallet(userDto);
    }

    @Transactional
    public SellerWallet createSellerWallet(CashSellerDto sellerDto) {
        return cashCreateSellerWalletUseCase.createSellerWallet(sellerDto);
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

//    @Transactional(readOnly = true)
//    public UserWalletResponseDto findUserWallet(Long userId) {
//        return cashGetWalletInfoUseCase.getUserWalletInfo(userId);
//    }

    @Transactional(readOnly = true)
    public BigDecimal findUserBalance(Long userId) {
        return cashGetBalanceUseCase.getUserWalletBalance(userId);
    }

//    @Transactional(readOnly = true)
//    public SellerWalletResponseDto findSellerWallet(Long sellerId) {
//        return cashGetSellerWalletInfoUseCase.getSellerWalletInfo(sellerId);
//    }

    @Transactional(readOnly = true)
    public BigDecimal findSellerBalance(Long sellerId) {
        return cashGetSellerBalanceUseCase.getSellerBalance(sellerId);
    }

    public List<UserCashLog> findAllCashLogs(Long userId) {
        return cashGetLogsUseCase.findCashLog(userId);
    }
}
