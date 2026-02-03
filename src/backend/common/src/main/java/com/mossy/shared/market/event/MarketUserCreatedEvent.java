package com.mossy.shared.market.event;

import com.mossy.shared.market.dto.event.MarketUserDto;

public record MarketUserCreatedEvent (
        MarketUserDto buyer
){}
