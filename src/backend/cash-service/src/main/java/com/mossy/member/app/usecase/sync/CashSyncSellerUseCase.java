package com.mossy.member.app.usecase.sync;

import com.mossy.member.domain.seller.CashSeller;
import com.mossy.member.out.seller.CashSellerRepository;
import com.mossy.member.out.seller.SellerWalletRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.cash.event.CashSellerCreatedEvent;
import com.mossy.shared.member.dto.event.SellerApprovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashSyncSellerUseCase {

    private final CashSellerRepository cashSellerRepository;
    private final SellerWalletRepository sellerWalletRepository;
    private final EventPublisher eventPublisher;

    public CashSeller syncSeller(SellerApprovedEvent seller) {
        // 1. CashSeller from 메서드를 사용하여 엔티티 생성 및 저장
        CashSeller cashSeller = cashSellerRepository.save(CashSeller.from(seller));

        // 2. 해당 판매자의 지갑이 없는 경우에만 지갑 생성 이벤트 발행
        if (!sellerWalletRepository.existsBySellerId(cashSeller.getId())) {
            eventPublisher.publish(
                new CashSellerCreatedEvent(cashSeller.toDto())
            );
        }

        return cashSeller;
    }
}