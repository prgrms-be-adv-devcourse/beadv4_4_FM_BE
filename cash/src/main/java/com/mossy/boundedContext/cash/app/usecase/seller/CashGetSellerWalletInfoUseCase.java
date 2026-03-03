package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.cash.in.dto.response.SellerWalletResponseDto;
import com.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashGetSellerWalletInfoUseCase {

    private final SellerWalletRepository sellerWalletRepository;
    private final CashMapper mapper;

    @Transactional(readOnly = true)
    public SellerWalletResponseDto getSellerWalletInfo(Long sellerId) {
        return sellerWalletRepository.findWalletBySellerId(sellerId)
            .map(mapper::toResponseDto)
            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_WALLET_NOT_FOUND));
    }
}