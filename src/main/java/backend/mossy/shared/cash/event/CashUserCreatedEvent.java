package backend.mossy.shared.cash.event;

import backend.mossy.shared.cash.dto.common.CashUserDto;

public record CashUserCreatedEvent (
    CashUserDto user
){}
