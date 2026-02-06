package com.mossy.boundedContext.cash.app;

import com.mossy.boundedContext.cash.domain.CashPolicy;
import com.mossy.boundedContext.cash.domain.seller.CashSeller;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.domain.user.CashUser;
import com.mossy.boundedContext.cash.domain.user.UserWallet;
import com.mossy.exception.DomainException;
import com.mossy.exception.CashErrorCode;
import com.mossy.boundedContext.cash.out.seller.CashSellerRepository;
import com.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import com.mossy.boundedContext.cash.out.user.CashUserRepository;
import com.mossy.boundedContext.cash.out.user.UserWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashSupport {

    private final UserWalletRepository userWalletRepository;
    private final SellerWalletRepository sellerWalletRepository;
    private final CashUserRepository cashUserRepository;
    private final CashSellerRepository cashSellerRepository;

     // --- [검증 관련] ---

    public void validateUserWalletExists(Long userId) {
        if (userWalletRepository.existsWalletByUserId(userId)) {
            throw new DomainException(CashErrorCode.WALLET_ALREADY_EXISTS);
        }
    }

    public void validateSellerWalletExists(Long sellerId) {
        if (sellerWalletRepository.existsBySellerId(sellerId)) {
            throw new DomainException(CashErrorCode.WALLET_ALREADY_EXISTS);
        }
    }

    // --- [검색 관련] ---

    public CashUser findCashUserById(Long userId) {
        return cashUserRepository.findCashUserById(userId)
            .orElseThrow(() -> new DomainException(CashErrorCode.USER_NOT_FOUND));
    }

    public CashSeller findCashSellerById(Long sellerId) {
        return cashSellerRepository.findCashSellerById(sellerId)
            .orElseThrow(() -> new DomainException(CashErrorCode.SELLER_NOT_FOUND));
    }

    public UserWallet findWalletByUserId(Long userId) {
        return userWalletRepository.findWalletByUserId(userId)
            .orElseThrow(() -> new DomainException(CashErrorCode.USER_WALLET_NOT_FOUND));
    }

    public SellerWallet findWalletBySellerId(Long sellerId) {
        return sellerWalletRepository.findWalletBySellerId(sellerId)
            .orElseThrow(() -> new DomainException(CashErrorCode.SELLER_WALLET_NOT_FOUND));
    }

    public SellerWallet findSystemWallet() {
        return sellerWalletRepository.findBySellerId(CashPolicy.SYSTEM_MEMBER_ID)
            .orElseThrow(() -> new DomainException(CashErrorCode.SELLER_WALLET_NOT_FOUND));
    }

    public SellerWallet findHoldingWallet() {
        return sellerWalletRepository.findBySellerId(CashPolicy.HOLDING_MEMBER_ID)
            .orElseThrow(() -> new DomainException(CashErrorCode.SELLER_WALLET_NOT_FOUND));
    }

    public SellerWallet findDonationWallet() {
        return sellerWalletRepository.findBySellerId(CashPolicy.DONATION_MEMBER_ID)
            .orElseThrow(() -> new DomainException(CashErrorCode.SELLER_WALLET_NOT_FOUND));
    }

    public SellerWallet findDeliveryWallet() {
        return sellerWalletRepository.findBySellerId(CashPolicy.DELIVERY_MEMBER_ID)
            .orElseThrow(() -> new DomainException(CashErrorCode.SELLER_WALLET_NOT_FOUND));
    }

}
