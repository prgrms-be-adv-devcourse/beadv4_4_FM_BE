package backend.mossy.shared.market.event;

import backend.mossy.shared.market.dto.common.MarketUserDto;

public record MarketUserModifiedEvent (
        MarketUserDto buyer
) {}
