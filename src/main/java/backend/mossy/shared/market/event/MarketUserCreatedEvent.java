package backend.mossy.shared.market.event;

import backend.mossy.shared.market.dto.common.MarketUserDto;

public record MarketUserCreatedEvent (
        MarketUserDto buyer
){}
