package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.mapper.CashPayloadMapper;
import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.cash.in.dto.response.SellerWalletResponseDto;
import com.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashGetSellerWalletInfoUseCase {

    private final SellerWalletRepository sellerWalletRepository;
    private final CashPayloadMapper mapper;

    public SellerWalletResponseDto getSellerWalletInfo(Long sellerId) {
        return sellerWalletRepository.findWalletBySellerId(sellerId)
            .map(mapper::toResponseDto)
            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_WALLET_NOT_FOUND));
    }
}