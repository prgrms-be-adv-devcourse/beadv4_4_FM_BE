package com.mossy.boundedContext.cash.app;

import com.mossy.boundedContext.cash.domain.CashPolicy;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.domain.user.UserWallet;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import com.mossy.boundedContext.cash.out.user.UserWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashSupport {

    private final UserWalletRepository userWalletRepository;
    private final SellerWalletRepository sellerWalletRepository;

     // --- [검증 관련] ---

    public void validateUserWalletExists(Long userId) {
        if (userWalletRepository.existsWalletByUserId(userId)) {
            throw new DomainException(ErrorCode.WALLET_ALREADY_EXISTS);
        }
    }

    public void validateSellerWalletExists(Long sellerId) {
        if (sellerWalletRepository.existsBySellerId(sellerId)) {
            throw new DomainException(ErrorCode.WALLET_ALREADY_EXISTS);
        }
    }

    // --- [조회 + 비관적 락 (잔액 변경 시 사용)] ---

    public UserWallet findWalletByUserIdForUpdate(Long userId) {
        return userWalletRepository.findWalletByUserIdForUpdate(userId)
            .orElseThrow(() -> new DomainException(ErrorCode.USER_WALLET_NOT_FOUND));
    }

    public SellerWallet findWalletBySellerIdForUpdate(Long sellerId) {
        return sellerWalletRepository.findWalletBySellerIdForUpdate(sellerId)
            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_WALLET_NOT_FOUND));
    }


    public SellerWallet findHoldingWallet() {
        return sellerWalletRepository.findBySellerIdForUpdate(CashPolicy.HOLDING_MEMBER_ID)
            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_WALLET_NOT_FOUND));
    }
}
