package com.mossy.boundedContext.cash.in.dto.event;

import com.mossy.boundedContext.cash.in.dto.command.CashUserDto;

public record CashUserCreatedEvent(
    CashUserDto user
) {
}