package com.mossy.shared.cash.event;

import com.mossy.shared.cash.dto.event.CashUserDto;

public record CashUserCreatedEvent (
    CashUserDto user
){}
