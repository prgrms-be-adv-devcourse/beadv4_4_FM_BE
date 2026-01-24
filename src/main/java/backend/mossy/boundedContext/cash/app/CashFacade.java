package backend.mossy.boundedContext.cash.app;

import backend.mossy.boundedContext.cash.app.usecase.seller.CashCreateSellerWalletUseCase;
import backend.mossy.boundedContext.cash.app.usecase.seller.CashCreditSellerBalanceUseCase;
import backend.mossy.boundedContext.cash.app.usecase.seller.CashDeductSellerBalanceUseCase;
import backend.mossy.boundedContext.cash.app.usecase.seller.CashGetSellerBalanceUseCase;
import backend.mossy.boundedContext.cash.app.usecase.seller.CashGetSellerWalletInfoUseCase;
import backend.mossy.boundedContext.cash.app.usecase.sync.CashSyncSellerUseCase;
import backend.mossy.boundedContext.cash.app.usecase.sync.CashSyncUserUseCase;
import backend.mossy.boundedContext.cash.app.usecase.user.CashCreateUserWalletUseCase;
import backend.mossy.boundedContext.cash.app.usecase.user.CashCreditUserBalanceUseCase;
import backend.mossy.boundedContext.cash.app.usecase.user.CashDeductUserBalanceUseCase;
import backend.mossy.boundedContext.cash.app.usecase.user.CashGetBalanceUseCase;
import backend.mossy.boundedContext.cash.app.usecase.user.CashGetWalletInfoUseCase;
import backend.mossy.boundedContext.cash.app.usecase.user.CashHoldingUseCase;
import backend.mossy.boundedContext.cash.domain.seller.CashSeller;
import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import backend.mossy.boundedContext.cash.domain.user.CashUser;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.shared.cash.dto.event.CashSellerDto;
import backend.mossy.shared.cash.dto.event.CashUserDto;
import backend.mossy.shared.cash.dto.request.SellerBalanceRequestDto;
import backend.mossy.shared.cash.dto.request.UserBalanceRequestDto;
import backend.mossy.shared.cash.dto.response.SellerWalletResponseDto;
import backend.mossy.shared.cash.dto.response.UserWalletResponseDto;
import backend.mossy.shared.member.dto.event.SellerApprovedEvent;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
import backend.mossy.shared.market.event.PaymentRefundEvent;
import backend.mossy.shared.member.dto.event.UserDto;
import java.math.BigDecimal;
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
}
