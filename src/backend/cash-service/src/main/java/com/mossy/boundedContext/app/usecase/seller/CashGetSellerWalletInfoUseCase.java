package com.mossy.boundedContext.app.usecase.seller;

import com.mossy.boundedContext.out.seller.SellerWalletRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.shared.cash.dto.response.SellerWalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashGetSellerWalletInfoUseCase {

    private final SellerWalletRepository sellerWalletRepository;

//    public SellerWalletResponseDto getSellerWalletInfo(Long sellerId) {
//        return sellerWalletRepository.findWalletBySellerId(sellerId)
//            .map(SellerWalletResponseDto::from)
//            .orElseThrow(
//                () -> new DomainException("NOT_FOUND_SELLER_WALLET", "판매자의 지갑 정보를 찾을 수 없습니다." + sellerId));
//    }
}