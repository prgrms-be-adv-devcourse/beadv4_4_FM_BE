package com.mossy.shared.market.event;

import com.mossy.shared.market.dto.event.MarketSellerDto;

public record MarketSellerCreatedEvent(
        MarketSellerDto seller
){ }
