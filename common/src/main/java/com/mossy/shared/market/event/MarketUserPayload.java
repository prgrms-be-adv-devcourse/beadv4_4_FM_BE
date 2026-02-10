package com.mossy.shared.market.event;

import com.mossy.shared.market.payload.MarketUserDto;

public record MarketUserPayload(
        MarketUserDto buyer
){}
