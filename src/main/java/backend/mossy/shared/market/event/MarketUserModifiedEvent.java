package backend.mossy.shared.market.event;

import backend.mossy.shared.market.dto.event.MarketUserDto;

public record MarketUserModifiedEvent (
        MarketUserDto buyer
) {}
