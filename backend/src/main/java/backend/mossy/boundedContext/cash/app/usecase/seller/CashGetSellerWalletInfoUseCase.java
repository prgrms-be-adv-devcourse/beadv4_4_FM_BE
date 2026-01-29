package backend.mossy.boundedContext.cash.app.usecase.seller;

import backend.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.shared.cash.dto.response.SellerWalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashGetSellerWalletInfoUseCase {

    private final SellerWalletRepository sellerWalletRepository;

    public SellerWalletResponseDto getSellerWalletInfo(Long sellerId) {
        return sellerWalletRepository.findWalletBySellerId(sellerId)
            .map(SellerWalletResponseDto::from)
            .orElseThrow(
                () -> new DomainException("NOT_FOUND_SELLER_WALLET", "판매자의 지갑 정보를 찾을 수 없습니다." + sellerId));
    }
}