package backend.mossy.shared.market.event;

import backend.mossy.shared.market.dto.MarketUserDto;

public record MarketUserCreatedEvent (
        MarketUserDto buyer
){}
