package com.mossy.member.app.market;

import com.mossy.member.domain.market.MarketSeller;
import com.mossy.member.out.market.MarketSellerRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.market.event.MarketSellerCreatedEvent;
import com.mossy.shared.member.dto.event.SellerApprovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketSyncSellerUseCase {

    private final MarketSellerRepository marketSellerRepository;
    private final EventPublisher eventPublisher;

    public MarketSeller syncSeller(SellerApprovedEvent seller) {
        boolean isNew = !marketSellerRepository.existsById(seller.id());

        MarketSeller marketSeller = marketSellerRepository.save(MarketSeller.from(seller));

        if (isNew) {
            eventPublisher.publish(new MarketSellerCreatedEvent(marketSeller.toDto()));
        }

        return marketSeller;
    }
}
