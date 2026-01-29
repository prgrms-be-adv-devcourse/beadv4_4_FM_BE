package backend.mossy.shared.market.event;

import backend.mossy.shared.market.dto.event.MarketSellerDto;

public record MarketSellerCreatedEvent(
        MarketSellerDto seller
){ }
