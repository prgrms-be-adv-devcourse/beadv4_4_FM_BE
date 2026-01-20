package backend.mossy.boundedContext.market.app.market;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.out.market.MarketSellerRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.market.event.MarketSellerCreatedEvent;
import backend.mossy.shared.member.dto.event.SellerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketSyncSellerUseCase {

    private final MarketSellerRepository marketSellerRepository;
    private final EventPublisher eventPublisher;

    public MarketSeller syncSeller(SellerDto seller) {
        boolean isNew = !marketSellerRepository.existsById(seller.id());

        MarketSeller marketSeller = marketSellerRepository.save(MarketSeller.from(seller));

        if (isNew) {
            eventPublisher.publish(new MarketSellerCreatedEvent(marketSeller.toDto()));
        }

        return marketSeller;
    }
}
