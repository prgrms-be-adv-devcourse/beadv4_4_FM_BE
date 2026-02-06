package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.mapper.CashPayloadMapper;
import com.mossy.boundedContext.cash.domain.seller.CashSeller;
import com.mossy.boundedContext.cash.out.seller.CashSellerRepository;
import com.mossy.boundedContext.cash.out.seller.SellerWalletRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.cash.event.CashSellerCreatedEvent;
import com.mossy.shared.member.payload.SellerPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashSyncSellerUseCase {

    private final CashSellerRepository cashSellerRepository;
    private final SellerWalletRepository sellerWalletRepository;
    private final EventPublisher eventPublisher;
    private final CashPayloadMapper mapper;

    public CashSeller syncSeller(SellerPayload seller) {
        CashSeller cashSeller = cashSellerRepository.save(mapper.toEntity(seller));

        if (!sellerWalletRepository.existsBySellerId(cashSeller.getId())) {
            eventPublisher.publish(
                new CashSellerCreatedEvent(mapper.toPayload(cashSeller))
            );
        }
        return cashSeller;
    }
}