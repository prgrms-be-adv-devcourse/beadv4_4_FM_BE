package com.mossy.boundedContext.marketUser.in.dto.event;

import com.mossy.boundedContext.marketUser.in.dto.command.MarketUserDto;

public record MarketUserPayload(
        MarketUserDto buyer
){}
