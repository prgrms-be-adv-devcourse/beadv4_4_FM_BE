package com.mossy.member.app;

import com.mossy.member.domain.CashPolicy;
import com.mossy.member.domain.seller.CashSeller;
import com.mossy.member.domain.seller.SellerWallet;
import com.mossy.member.domain.user.CashUser;
import com.mossy.member.domain.user.UserWallet;
import com.mossy.member.out.seller.CashSellerRepository;
import com.mossy.member.out.seller.SellerWalletRepository;
import com.mossy.member.out.user.CashUserRepository;
import com.mossy.member.out.user.UserWalletRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashSupport {

    private final UserWalletRepository userWalletRepository;
    private final SellerWalletRepository sellerWalletRepository;
    private final CashUserRepository cashUserRepository;
    private final CashSellerRepository cashSellerRepository;

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

    public CashUser findCashUserById(Long userId) {
        return cashUserRepository.findCashUserById(userId)
            .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));
    }

    public CashSeller findCashSellerById(Long sellerId) {
        return cashSellerRepository.findCashSellerById(sellerId)
            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_NOT_FOUND));
    }

    public UserWallet findWalletByUserId(Long userId) {
        return userWalletRepository.findWalletByUserId(userId)
            .orElseThrow(() -> new DomainException(ErrorCode.USER_WALLET_NOT_FOUND));
    }

    public SellerWallet findWalletBySellerId(Long sellerId) {
        return sellerWalletRepository.findWalletBySellerId(sellerId)
            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_WALLET_NOT_FOUND));
    }

    public SellerWallet findHoldingWallet() {
        return sellerWalletRepository.findBySellerId(CashPolicy.HOLDING_MEMBER_ID)
            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_WALLET_NOT_FOUND));
    }
}
