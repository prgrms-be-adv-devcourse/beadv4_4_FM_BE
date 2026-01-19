package backend.mossy.boundedContext.cash.app.usecase.sync;

import backend.mossy.boundedContext.cash.domain.seller.CashSeller;
import backend.mossy.boundedContext.cash.out.seller.CashSellerRepository;
import backend.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.cash.event.CashSellerCreatedEvent;
import backend.mossy.shared.member.dto.event.SellerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashSyncSellerUseCase {

    private final CashSellerRepository cashSellerRepository;
    private final SellerWalletRepository sellerWalletRepository;
    private final EventPublisher eventPublisher;

    public CashSeller syncSeller(SellerDto seller) {
        // 1. CashSeller 빌더를 사용하여 엔티티 생성 및 저장
        CashSeller cashSeller = cashSellerRepository.save(
            CashSeller.builder()
                .id(seller.id())
                .createdAt(seller.createdAt())
                .updatedAt(seller.updatedAt())
                .userId(seller.userId())
                .sellerType(seller.sellerType())
                .storeName(seller.storeName())
                .businessNum(seller.businessNum())
                .representativeName(seller.representativeName())
                .contactEmail(seller.contactEmail())
                .contactPhone(seller.contactPhone())
                .address1(seller.address1())
                .address2(seller.address2())
                .status(seller.status())
                .build()
        );

        // 2. 해당 판매자의 지갑이 없는 경우에만 지갑 생성 이벤트 발행
        if (!sellerWalletRepository.existsBySellerId(cashSeller.getId())) {
            eventPublisher.publish(
                new CashSellerCreatedEvent(cashSeller.toDto())
            );
        }

        return cashSeller;
    }
}