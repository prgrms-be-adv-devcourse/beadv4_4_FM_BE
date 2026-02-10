package com.mossy.boundedContext.cash.in.dto.event;

import com.mossy.boundedContext.cash.in.dto.command.CashSellerDto;

public record CashSellerCreatedEvent(
    CashSellerDto seller
) {}
