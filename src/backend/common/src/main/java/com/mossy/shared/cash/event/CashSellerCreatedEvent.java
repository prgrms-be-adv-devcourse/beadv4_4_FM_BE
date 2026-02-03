package com.mossy.shared.cash.event;

import com.mossy.shared.cash.dto.event.CashSellerDto;

public record CashSellerCreatedEvent(
    CashSellerDto seller
) {}
