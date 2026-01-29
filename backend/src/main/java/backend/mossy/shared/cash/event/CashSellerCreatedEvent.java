package backend.mossy.shared.cash.event;

import backend.mossy.shared.cash.dto.event.CashSellerDto;

public record CashSellerCreatedEvent(
    CashSellerDto seller
) {}
