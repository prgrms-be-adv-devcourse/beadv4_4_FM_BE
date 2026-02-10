package com.mossy.shared.market.event;

import com.mossy.shared.market.payload.MarketSellerDto;

public record MarketSellerPayload(
        MarketSellerDto seller
){ }
