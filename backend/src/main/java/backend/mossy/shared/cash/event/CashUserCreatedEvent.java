package backend.mossy.shared.cash.event;

import backend.mossy.shared.cash.dto.event.CashUserDto;

public record CashUserCreatedEvent (
    CashUserDto user
){}
